package com.syx.system.service.impl;

import com.syx.core.domains.*;
import com.syx.system.service.EftsExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author 宋远欣
 * @date 2022/3/22
 **/
@Service
public class EftsExcelServiceImpl implements EftsExcelService {

    private static final String MSGTYPE = "file";
    private static final String AGENTID = "1000038";
    private static final int SAFE = 0;
    private static final int ENABLE_DUPLICATE_CHECK = 1;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WxServiceImpl wxServiceImpl;

    public SendMsgRes sendEftsMsg(File excel){
        SendMsgRes sendMsgRes = new SendMsgRes();
        UploadTempFileRes uploadTempFileRes = uploadTempFile(excel);
        if (uploadTempFileRes.getErrcode() == 0){
            sendMsgRes.setErrmsg("上传临时素材失败！");
        }else {
            SendEftsExcelData data = new SendEftsExcelData();
            data.setTouser("00072403");
            EftsMsgData eftsMsgData = new EftsMsgData();
            eftsMsgData.setMedia_id(uploadTempFileRes.getMedia_id());
            data.setFile(eftsMsgData);
            data.setMsgtype(MSGTYPE);
            data.setAgentid(AGENTID);
            data.setSafe(SAFE);
            data.setEnable_duplicate_check(ENABLE_DUPLICATE_CHECK);
            sendMsgRes = sendEftsExcel(data, redisTemplate.opsForValue().get("access_token").toString());
        }
        return sendMsgRes;
    }

    private SendMsgRes sendEftsExcel(SendEftsExcelData data, String accessToken) {
        SendMsgRes sendMsgRes = null;
        try {
            sendMsgRes = wxServiceImpl.sendEftsExcelMsg(data, accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sendMsgRes;
    }

    /**
     * 上传临时素材
     * @param file
     * @return
     */
    public UploadTempFileRes uploadTempFile(File file) {
        UploadTempFileRes uploadTempFileRes = new UploadTempFileRes();
        //判断Access_Token是否有效
        Boolean isToKen = redisTemplate.hasKey("access_token");
        if (!isToKen){
            getEftsAccessToken();
            //从redis中获取token
            String access_token = redisTemplate.opsForValue().get("access_token").toString();
            //调用企业微信接口上传临时素材
            uploadTempFile(file, access_token);
        }else {
            //从redis中获取token
            String access_token = redisTemplate.opsForValue().get("access_token").toString();
            //调用企业微信接口上传临时素材
            uploadTempFile(file, access_token);
        }

        return uploadTempFileRes;
    }

    /**
     * 获取发送资金系统相关excel应用Token
     */
    public void getEftsAccessToken(){
        AccessTokenRes accessTokenRes = new AccessTokenRes();
        try {
            accessTokenRes = wxServiceImpl.getEftsAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将access_token存入redis中,并设置失效时间为两小时
        redisTemplate.opsForValue().set("access_token",accessTokenRes.getAccess_token(),2, TimeUnit.HOURS);
    }

    public void uploadTempFile(File file, String access_token){
        UploadTempFileRes uploadTempFileRes = new UploadTempFileRes();
        try {
            uploadTempFileRes = wxServiceImpl.uploadTempFile(file, access_token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将media_id写入redis中，并设置失效时间为3天
        redisTemplate.opsForValue().set("media_id",uploadTempFileRes.getMedia_id(),3,TimeUnit.DAYS);
    }
}
