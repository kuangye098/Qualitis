package com.webank.wedatasphere.dss.appconn.qualitis.project;

import com.webank.wedatasphere.dss.appconn.qualitis.QualitisConf;
import com.webank.wedatasphere.dss.appconn.qualitis.QualitisExceptionUtils;
import com.webank.wedatasphere.dss.appconn.qualitis.action.QualitisPostAction;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.conf.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QualitisProjectCreationOperation implements ProjectCreationOperation, QualitisConf {

    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisProjectCreationOperation.class);

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    private StructureService structureService;

    private final static String projectUrl = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/exchangis/createProject";

    public QualitisProjectCreationOperation(StructureService structureService, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) {
        this.structureService = structureService;
        this.ssoRequestOperation = ssoRequestOperation;
    }

    public void init() {

    }

    public void setStructureService(StructureService structureService) {
        this.structureService = structureService;
    }

    public ProjectResponseRef createProject(ProjectRequestRef requestRef) throws ExternalOperationFailedException {

        String url = getBaseUrl() + projectUrl;

        LOGGER.info("project create operation .....ProjectRequestRef = {},projectUrl = {} " +
                "            , dssUrl = {}.",requestRef,this.projectUrl,url);

        QualitisProjectResponseRef responseRef = null;

        QualitisPostAction httpPost = new QualitisPostAction();
        httpPost.setUrl(projectUrl);
        httpPost.setUser(requestRef.getCreateBy());
        httpPost.addRequestPayload("workspaceName", requestRef.getWorkspaceName());
        httpPost.addRequestPayload("projectName", requestRef.getName());
        httpPost.addRequestPayload("createdBy", requestRef.getCreateBy());
        httpPost.addRequestPayload("description", requestRef.getDescription());
        httpPost.addRequestPayload("projectId", requestRef.getId());

        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(APP_NAME.getValue());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());

        HttpResult httpResponse;
        try {
            httpResponse = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation,httpPost);
            LOGGER.info("{}, exchangis {}", requestRef.getName(), httpResponse.getResponseBody());
            if(httpResponse.getStatusCode() == 200){
                responseRef = new QualitisProjectResponseRef(httpResponse.getResponseBody(),0);
                responseRef.setProjectRefId((Long)responseRef.toMap().get("projectId"));
            }else {
                QualitisExceptionUtils.dealErrorException(60051,
                        "failed to create project in exchangis : " + responseRef.getErrorMsg(),
                        ExternalOperationFailedException.class);
            }
        } catch (Throwable t) {
            QualitisExceptionUtils.dealErrorException(60051, "failed to create project in exchangis", t, ExternalOperationFailedException.class);
        }
        return responseRef;
    }

    private String getBaseUrl(){
        return structureService.getAppInstance().getBaseUrl();
    }
}
