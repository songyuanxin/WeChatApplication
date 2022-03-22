package com.syx.system.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;

import com.syx.commons.client.WxClient;
import com.syx.core.domains.AccessTokenRes;
import com.syx.core.domains.SendMsgData;
import com.syx.core.domains.SendMsgRes;
import com.syx.core.utils.JsonUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 宋远欣
 * @date 2022/3/1
 **/
@Service
public class WxServiceImpl{
    private static final String CORP_ID = "wx69ef55d633835331";
    private static final String CORP_SECRET = "QzM1PetgYBE7QcxsPJy6giWk5AgXH198wTHPYq31-q8";

    @SuppressWarnings("unchecked")
    public AccessTokenRes getAccessToken() throws IOException {
        WxClient client = new WxClient();
        Map<String, Object> params = new HashMap<>(2);
        params.put("corpid",CORP_ID);
        params.put("corpsecret",CORP_SECRET);
        String s = client.executeGet(WxClient.Api.ACCESS_TOKEN_GET, params);
        AccessTokenRes tokenBaseRes = (AccessTokenRes) JsonUtils.deserializeObject(s, new TypeReference<AccessTokenRes>() {});
        return tokenBaseRes;
    }

    @SuppressWarnings("unchecked")
    public SendMsgRes sendMsg(SendMsgData sendMsgData, String accessToken) throws IOException{
        WxClient client = new WxClient();
        Map<String, Object> params = new HashMap<>(1);
        params.put("access_token",accessToken);
        String s = client.executePost(WxClient.Api.SEND_MSG, sendMsgData, params);
        SendMsgRes sendMsgRes = (SendMsgRes) JsonUtils.deserializeObject(s, new TypeReference<SendMsgRes>() {});
        return sendMsgRes;
    }
}
