package com.syx.system.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;

import com.syx.commons.client.WxClient;
import com.syx.core.domains.*;
import com.syx.core.utils.JsonUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 宋远欣
 * @date 2022/3/1
 **/
@Service
public class WxServiceImpl{
    private static final String ATT_CORP_ID = "wx69ef55d633835331";
    private static final String ATT_CORP_SECRET = "QzM1PetgYBE7QcxsPJy6giWk5AgXH198wTHPYq31-q8";
    private static final String EFTS_CORP_ID = "wx69ef55d633835331";
    private static final String EFTS_CORP_SECRET = "QzM1PetgYBE7QcxsPJy6giWk5AgXH198wTHPYq31-q8";

    /**
     * 获取考勤提醒应用Token
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public AccessTokenRes getAttAccessToken() throws IOException {
        WxClient client = new WxClient();
        Map<String, Object> params = new HashMap<>(2);
        params.put("corpid",ATT_CORP_ID);
        params.put("corpsecret",ATT_CORP_SECRET);
        String s = client.executeGet(WxClient.Api.ACCESS_TOKEN_GET, params);
        AccessTokenRes tokenBaseRes = (AccessTokenRes) JsonUtils.deserializeObject(s, new TypeReference<AccessTokenRes>() {});
        return tokenBaseRes;
    }

    /**
     * 通过考勤提醒应用发送迟到提醒
     * @param sendMsgData
     * @param accessToken
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public SendMsgRes sendLateMsg(SendMsgData sendMsgData, String accessToken) throws IOException{
        WxClient client = new WxClient();
        Map<String, Object> params = new HashMap<>(1);
        params.put("access_token",accessToken);
        String s = client.executePost(WxClient.Api.SEND_MSG, sendMsgData, params);
        SendMsgRes sendMsgRes = (SendMsgRes) JsonUtils.deserializeObject(s, new TypeReference<SendMsgRes>() {});
        return sendMsgRes;
    }

    /**
     * 获取资金系统应用Token
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public AccessTokenRes getEftsAccessToken() throws IOException {
        WxClient client = new WxClient();
        Map<String, Object> params = new HashMap<>(2);
        params.put("corpid",EFTS_CORP_ID);
        params.put("corpsecret",EFTS_CORP_SECRET);
        String s = client.executeGet(WxClient.Api.ACCESS_TOKEN_GET, params);
        AccessTokenRes tokenBaseRes = (AccessTokenRes) JsonUtils.deserializeObject(s, new TypeReference<AccessTokenRes>() {});
        return tokenBaseRes;
    }

    /**
     * 调用企业微信接口上传临时素材
     * @param excel
     * @param accessToken
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public UploadTempFileRes uploadTempFile(File excel, String accessToken) throws IOException{
        WxClient client = new WxClient();
        Map<String, Object> params = new HashMap<>(2);
        params.put("access_token",accessToken);
        params.put("type", "file");
        String s = client.executePostUpload(WxClient.Api.UPLOAD_TEMP_FILE, excel, params);
        UploadTempFileRes uploadTempFileRes = (UploadTempFileRes) JsonUtils.deserializeObject(s, new TypeReference<UploadTempFileRes>() {});
        return uploadTempFileRes;
    }

    @SuppressWarnings("unchecked")
    public SendMsgRes sendEftsExcelMsg(SendEftsExcelData data, String accessToken) throws IOException{
        WxClient client = new WxClient();
        Map<String, Object> params = new HashMap<>(1);
        params.put("access_token",accessToken);
        String s = client.executePostEfts(WxClient.Api.SEND_MSG, data, params);
        SendMsgRes sendMsgRes = (SendMsgRes) JsonUtils.deserializeObject(s, new TypeReference<SendMsgRes>() {});
        return sendMsgRes;
    }
}
