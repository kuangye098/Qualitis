package com.webank.wedatasphere.dss.appconn.qualitis.project;

import com.webank.wedatasphere.dss.appconn.qualitis.QualitisConf;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.service.Operation;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QualitisProjectService extends ProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisProjectService.class);

    private AppInstance appInstance;

    private StructureIntegrationStandard structureIntegrationStandard;

    private Map<Class<? extends Operation>, Operation<?, ?>> operationMap = new ConcurrentHashMap<>();

    public Operation createOperation(Class<? extends Operation> clazz) {
        return null;
    }

    public boolean isOperationExists(Class<? extends Operation> clazz) {
        return false;
    }

    public boolean isOperationNecessary(Class<? extends Operation> clazz) {
        return false;
    }

    public void setAppStandard(StructureIntegrationStandard appStandard) {
        this.structureIntegrationStandard = appStandard;
    }

    public StructureIntegrationStandard getAppStandard() {
        return this.structureIntegrationStandard;
    }

    public AppInstance getAppInstance() {
        return this.appInstance;
    }

    public void setAppInstance(AppInstance appInstance) {
        this.appInstance = appInstance;
    }

    public ProjectCreationOperation createProjectCreationOperation() {
        SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation
                = getSSORequestService().createSSORequestOperation(QualitisConf.APP_NAME.getValue());
        return new QualitisProjectCreationOperation(this,ssoRequestOperation);
    }

    public ProjectUpdateOperation createProjectUpdateOperation() {
        SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation
                = getSSORequestService().createSSORequestOperation(QualitisConf.APP_NAME.getValue());
        return new QualitisProjectUpdateOperation(this,ssoRequestOperation);
    }

    public ProjectDeletionOperation createProjectDeletionOperation() {
        SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation
                = getSSORequestService().createSSORequestOperation(QualitisConf.APP_NAME.getValue());
        return new QualitisProjectDeleteOperation(this,ssoRequestOperation);
    }

    public ProjectUrlOperation createProjectUrlOperation() {
        return null;
    }
}
