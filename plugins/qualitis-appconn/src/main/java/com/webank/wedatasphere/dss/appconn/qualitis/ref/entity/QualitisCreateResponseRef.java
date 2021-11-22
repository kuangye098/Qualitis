package com.webank.wedatasphere.dss.appconn.qualitis.ref.entity;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.NumberUtils;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

import java.util.Map;

public class QualitisCreateResponseRef extends AbstractResponseRef {

    public QualitisCreateResponseRef(String responseBody) throws Exception {
        super(responseBody, 0);
        responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
        status = NumberUtils.getInt(responseMap.get("status"));
        if (status != 0) {
            errorMsg = responseMap.get("message").toString();
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return responseMap;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }
}
