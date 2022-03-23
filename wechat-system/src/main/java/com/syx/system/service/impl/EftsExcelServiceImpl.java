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

    /**
     * 调用企业微信接口发送文件
     * @param excel
     * @return
     */
    public SendMsgRes sendEftsMsg(File excel){
        //发送文件返回实体
        SendMsgRes sendMsgRes = new SendMsgRes();
        //上传临时素材
        UploadTempFileRes uploadTempFileRes = uploadTempFile(excel);
        //判断临时素材是否上传成功
        if (uploadTempFileRes.getErrcode() != 0){
            sendMsgRes.setErrmsg("上传临时素材失败！");
        }else {
            //发送文件消息实体
            SendEftsExcelData data = new SendEftsExcelData();
            //根据店编指定发送人
            data.setTouser("00072403");
            EftsMsgData eftsMsgData = new EftsMsgData();
            eftsMsgData.setMedia_id(redisTemplate.opsForValue().get("media_id").toString());
            data.setFile(eftsMsgData);
            data.setMsgtype(MSGTYPE);
            data.setAgentid(AGENTID);
            data.setSafe(SAFE);
            data.setEnable_duplicate_check(ENABLE_DUPLICATE_CHECK);
            //发送消息
            sendMsgRes = sendEftsExcel(data, redisTemplate.opsForValue().get("access_token").toString());
        }
        return sendMsgRes;
    }

    /**
     * 企业微信发送文件消息
     * @param data
     * @param accessToken
     * @return
     */
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

    /**
     * 上传临时素材，并将上传成功后的media_id存入redis中，设置失效时长为3天
     * @param file
     * @param access_token
     */
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
