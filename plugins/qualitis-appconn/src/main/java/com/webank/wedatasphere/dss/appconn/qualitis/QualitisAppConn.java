package com.webank.wedatasphere.dss.appconn.qualitis;

import com.webank.wedatasphere.dss.appconn.core.ext.ThirdlyAppConn;
import com.webank.wedatasphere.dss.appconn.core.impl.AbstractOnlySSOAppConn;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.appconn.qualitis.standard.QualitisDevelopmentIntergrationStandard;
import com.webank.wedatasphere.dss.appconn.qualitis.standard.QualitisStructureIntegrationStandard;

public class QualitisAppConn extends AbstractOnlySSOAppConn implements ThirdlyAppConn {

    private DevelopmentIntegrationStandard developmentIntegrationStandard;
    private StructureIntegrationStandard structureIntegrationStandard;


    @Override
    protected void initialize() {
        developmentIntegrationStandard = new QualitisDevelopmentIntergrationStandard();
        structureIntegrationStandard = new QualitisStructureIntegrationStandard();
    }


    @Override
    public DevelopmentIntegrationStandard getOrCreateDevelopmentStandard() {
        return developmentIntegrationStandard;
    }

    @Override
    public StructureIntegrationStandard getOrCreateStructureStandard() {
        return structureIntegrationStandard;
    }
}