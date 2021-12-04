package com.webank.wedatasphere.dss.appconn.qualitis.project;

import com.webank.wedatasphere.dss.appconn.qualitis.QualitisConf;
import com.webank.wedatasphere.dss.appconn.qualitis.QualitisExceptionUtils;
import com.webank.wedatasphere.dss.appconn.qualitis.action.QualitisDeleteAction;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.conf.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QualitisProjectDeleteOperation implements ProjectDeletionOperation, QualitisConf {

    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisProjectDeleteOperation.class);

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    private StructureService structureService;

    private final static String projectUrl = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/qualitis/projects/{}";

    public QualitisProjectDeleteOperation(StructureService structureService, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) {
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
    public ResponseRef deleteProject(ProjectRequestRef requestRef) throws ExternalOperationFailedException {

        String actualProjectUrl = String.format(projectUrl,requestRef.getId());
        String url = getBaseUrl() + actualProjectUrl;

        LOGGER.info("project delete operation .....ProjectRequestRef = {},projectUrl = {} " +
                "            , dssUrl = {}.",requestRef,actualProjectUrl,url);

        QualitisProjectResponseRef responseRef = null;

        QualitisDeleteAction deleteAction = new QualitisDeleteAction();
        deleteAction.setUrl(actualProjectUrl);
        deleteAction.setUser(requestRef.getCreateBy());
        deleteAction.setParameter("project_id", requestRef.getId());

        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(APP_NAME.getValue());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());

        HttpResult httpResponse;
        try {
            httpResponse = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation,deleteAction);
            LOGGER.info("{}, qualitis {}", requestRef.getName(), httpResponse.getResponseBody());
            if(httpResponse.getStatusCode() == 200){
                responseRef = new QualitisProjectResponseRef(httpResponse.getResponseBody(),0);
                responseRef.setProjectRefId((Long)responseRef.toMap().get("projectId"));
            }else {
                QualitisExceptionUtils.dealErrorException(60051,
                        "failed to delete project in exchangis : " + responseRef.getErrorMsg(),
                        ExternalOperationFailedException.class);
            }
        } catch (Throwable t) {
            QualitisExceptionUtils.dealErrorException(60051, "failed to delete project in qualitis", t, ExternalOperationFailedException.class);
        }
        return responseRef;
    }
}
