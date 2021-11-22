package com.webank.wedatasphere.dss.appconn.qualitis.ref.operation;


import com.webank.wedatasphere.dss.appconn.qualitis.QualitisConf;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CreateRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.appconn.qualitis.action.QualitisPostAction;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.entity.QualitisCreateRequestRef;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.entity.QualitisCreateResponseRef;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QualitisRefCreationOperation implements RefCreationOperation<CreateRequestRef> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisRefCreationOperation.class);

    private DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public QualitisRefCreationOperation(DevelopmentService developmentService) {
        this.developmentService = developmentService;
        this.ssoRequestOperation = new OriginSSORequestOperation(QualitisConf.APP_NAME.getValue());
    }

    @Override
    public ResponseRef createRef(CreateRequestRef requestRef) throws ExternalOperationFailedException {
        QualitisCreateRequestRef createRequestRef = (QualitisCreateRequestRef) requestRef;
        createRequestRef.setParameter("example-param1", "nutsjian");
        createRequestRef.setParameter("example-param2", "32");
        createRequestRef.setParameter("example-param3", "male");
        return requestCall(createRequestRef);
    }

    // 同步请求三方系统
    private ResponseRef requestCall(QualitisCreateRequestRef requestRef) throws ExternalOperationFailedException {

        QualitisPostAction qualitisPostAction = new QualitisPostAction();
        qualitisPostAction.addRequestPayload("name", requestRef.getName());
        qualitisPostAction.addRequestPayload("projectId", requestRef.getParameter("projectId"));

        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(QualitisConf.APP_NAME.getValue());
        ssoUrlBuilderOperation.setReqUrl(getBaseUrl() + "/development/create");
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());

        ResponseRef responseRef;
        try{
            LOGGER.info("exchangisPostAction  =>  {},ssoUrlBuilderOperation builtUrl => {}",
                    qualitisPostAction,ssoUrlBuilderOperation.getBuiltUrl());
            qualitisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            //
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, qualitisPostAction);
            responseRef = new QualitisCreateResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(90177, "Create Widget Exception", e);
        }
        // cs
//        VisualisRefUpdateOperation visualisRefUpdateOperation = new VisualisRefUpdateOperation(developmentService);
//        VisualisUpdateCSRequestRef visualisUpdateCSRequestRef = new VisualisUpdateCSRequestRef();
//        visualisUpdateCSRequestRef.setContextID((String) requestRef.getJobContent().get(CSCommonUtils.CONTEXT_ID_STR));
//        visualisUpdateCSRequestRef.setJobContent(responseRef.toMap());
//        visualisUpdateCSRequestRef.setUserName(requestRef.getUsername());
//        visualisUpdateCSRequestRef.setNodeType(requestRef.getNodeType());
//        visualisUpdateCSRequestRef.setWorkspace(requestRef.getWorkspace());
//        visualisRefUpdateOperation.updateRef(visualisUpdateCSRequestRef);
        return responseRef;
    }

    private String getBaseUrl() {
        return this.developmentService.getAppInstance().getBaseUrl();
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
