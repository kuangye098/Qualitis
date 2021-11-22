package com.webank.wedatasphere.dss.appconn.qualitis.standard;

import com.webank.wedatasphere.dss.appconn.qualitis.ref.service.QualitisCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.service.*;
import com.webank.wedatasphere.dss.standard.app.development.standard.AbstractDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class QualitisDevelopmentIntergrationStandard extends AbstractDevelopmentIntegrationStandard {

    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisDevelopmentIntergrationStandard.class);

    @Override
    protected RefCRUDService createRefCRUDService() {
        return new QualitisCRUDService();
    }

    @Override
    protected RefExecutionService createRefExecutionService() {
        return null;
    }

    @Override
    protected RefExportService createRefExportService() {
        return null;
    }

    @Override
    protected RefImportService createRefImportService() {
        return null;
    }

    @Override
    protected RefQueryService createRefQueryService() {
        return null;
    }

    @Override
    public void init() throws AppStandardErrorException {

    }

    @Override
    public void close() throws IOException {

    }
}
