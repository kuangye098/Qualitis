package com.webank.wedatasphere.dss.appconn.qualitis;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

public interface QualitisConf {
    public static final CommonVars<String> DSS_URL = CommonVars.apply("wds.dss.appconn.qualitis.dss.url", "http://127.0.0.1:8088/");

    public static final CommonVars<String> APP_NAME = CommonVars.apply("wds.dss.appconn.qualitis.app.name", "exchangis");
}
