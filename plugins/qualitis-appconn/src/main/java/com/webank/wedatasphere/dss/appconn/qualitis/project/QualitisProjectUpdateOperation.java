package com.webank.wedatasphere.dss.appconn.qualitis.project;

import com.webank.wedatasphere.dss.appconn.qualitis.QualitisConf;
import com.webank.wedatasphere.dss.appconn.qualitis.QualitisExceptionUtils;
import com.webank.wedatasphere.dss.appconn.qualitis.action.QualitisPutAction;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.conf.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QualitisProjectUpdateOperation implements ProjectUpdateOperation, QualitisConf {

    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisProjectUpdateOperation.class);

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    private StructureService structureService;

    private final static String projectUrl = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/exchangis/updateProject";

    public QualitisProjectUpdateOperation(StructureService structureService, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) {
        this.structureService = structureService;
        this.ssoRequestOperation = ssoRequestOperation;
    }

    public void init() {

    }

    public void setStructureService(StructureService structureService) {
        this.structureService = structureService;
    }

    private String getBaseUrl(){
        return structureService.getAppInstance().getBaseUrl();
    }

    @Override
    public ProjectResponseRef updateProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {

        String url = getBaseUrl() + projectUrl;

        LOGGER.info("project update operation .....ProjectRequestRef = {},projectUrl = {} " +
                "            , dssUrl = {}.",projectRequestRef,this.projectUrl,url);

        QualitisProjectResponseRef responseRef = null;

        QualitisPutAction putAction = new QualitisPutAction();
        putAction.setUrl(projectUrl);
        putAction.setUser(projectRequestRef.getCreateBy());
        putAction.addRequestPayload("workspaceName", projectRequestRef.getWorkspaceName());
        putAction.addRequestPayload("projectName", projectRequestRef.getName());
        putAction.addRequestPayload("createdBy", projectRequestRef.getCreateBy());
        putAction.addRequestPayload("description", projectRequestRef.getDescription());
        putAction.addRequestPayload("id", projectRequestRef.getId());

        SSOUrlBuilderOperation ssoUrlBuilderOperation = projectRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(APP_NAME.getValue());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(projectRequestRef.getWorkspace().getWorkspaceName());

        HttpResult httpResponse;
        try {
            httpResponse = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation,putAction);
            LOGGER.info("{}, exchangis {}", projectRequestRef.getName(), httpResponse.getResponseBody());
            if(httpResponse.getStatusCode() == 200){
                responseRef = new QualitisProjectResponseRef(httpResponse.getResponseBody(),0);
                responseRef.setProjectRefId((Long)responseRef.toMap().get("id"));
            }else {
                QualitisExceptionUtils.dealErrorException(60051,
                        "failed to update project in exchangis : " + responseRef.getErrorMsg(),
                        ExternalOperationFailedException.class);
            }
        } catch (Throwable t) {
            QualitisExceptionUtils.dealErrorException(60051, "failed to update project in exchangis", t, ExternalOperationFailedException.class);
        }
        return responseRef;
    }
}
