package com.syx.system.service.impl;

import com.syx.core.domains.*;
import com.syx.system.mapper.AttendanceMapper;
import com.syx.system.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 宋远欣
 * @date 2022/3/1
 **/
@Service
public class AttendanceServiceImpl implements AttendanceService {

    private static final String MSGTYPE = "text";
    private static final String AGENTID = "1000038";
    private static final int SAFE = 0;
    private static final int ENABLE_DUPLICATE_CHECK = 1;


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WxServiceImpl wxServiceImpl;

    @Autowired
    private AttendanceMapper attendanceMapper;

    /**
     * 发送迟到消息
     * @return
     */
    @Override
    public SendMsgRes sendLateMsg() {
        SendMsgRes sendMsgRes = new SendMsgRes();
        //一、获取迟到用户信息
        List<WMsgLog> msgLog = attendanceMapper.getMsgLog();
        //二、若获取到当日迟到用户,则给消息实体赋值
        if (msgLog.size() == 0){
            sendMsgRes = new SendMsgRes();
            sendMsgRes.setErrcode(1);
            sendMsgRes.setErrmsg("没有员工需要发迟到提醒！");
            return sendMsgRes;
        }
        for (WMsgLog lateMsg:msgLog){
            SendMsgData sendMsgData = new SendMsgData();
            MsgData msgData = new MsgData();
            sendMsgData.setAgentid(AGENTID);
            sendMsgData.setMsgtype(MSGTYPE);
            sendMsgData.setSafe(SAFE);
            sendMsgData.setEnable_duplicate_check(ENABLE_DUPLICATE_CHECK);
//            sendMsgData.setTouser("00" + lateMsg.getPernr());
            sendMsgData.setTouser("00072403");
            msgData.setContent("迟到提醒:\n" + lateMsg.getMsgData());
            sendMsgData.setText(msgData);
            //三、调用企业微信接口发送提醒消息
            //判断redis中的token是否有效
            Boolean access_token1 = redisTemplate.hasKey("access_token");
            //若token无效则调用企业微信接口获取access_token并再次写入redis
            if (!access_token1){
                getAccessToken();
                //从redis中获取token
                String access_token = redisTemplate.opsForValue().get("access_token").toString();
                //调用企业微信接口发送消息
                SendMsgRes sendMsgRes1 = sendLateMsg(sendMsgData, access_token);
                //判断提醒消息是否发送成功，若发送成功则将发送时间写入数据库
                int result = setSendMsgTime(lateMsg.getPernr(), sendMsgRes);
                if (result != 1){
                    sendMsgRes.setErrcode(1);
                    sendMsgRes.setErrmsg("发送时间写入数据库失败");
                    return sendMsgRes;
                }
                sendMsgRes = sendMsgRes1;
            }else {
                //若token有效，则直接调用企业微信接口发送消息
                String access_token = redisTemplate.opsForValue().get("access_token").toString();
                //调用企业微信接口发送消息
                SendMsgRes sendMsgRes1 = sendLateMsg(sendMsgData, access_token);
                //判断提醒消息是否发送成功，若发送成功则将发送时间写入数据库
                int result = setSendMsgTime(lateMsg.getPernr(), sendMsgRes);
                if (result != 1){
                    sendMsgRes.setErrcode(1);
                    sendMsgRes.setErrmsg("发送时间写入数据库失败");
                    return sendMsgRes;
                }
                sendMsgRes = sendMsgRes1;
            }
        }
        return sendMsgRes;
    }

    /**
     * 将发送时间写入数据库
     * @param sendMsgRes
     * @return
     */
    private int setSendMsgTime(String pernr,SendMsgRes sendMsgRes) {
        if (sendMsgRes.getErrcode() != 0){
            return 2;
        }
        //获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        //时间格式化
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = localDateTime.format(formatter);
        //将时间转为timestamp类型
        Timestamp timestampNow = new Timestamp(System.currentTimeMillis());
        try {
            timestampNow = Timestamp.valueOf(now);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attendanceMapper.setSendMsgTime(pernr,timestampNow);
    }

    /**
     * 获取企业微信应用token
     * @return
     */
    public void getAccessToken(){
        AccessTokenRes accessTokenRes = new AccessTokenRes();
        try {
            accessTokenRes = wxServiceImpl.getAttAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将access_token存入redis中,并设置失效时间为两小时
        redisTemplate.opsForValue().set("access_token",accessTokenRes.getAccess_token(),2, TimeUnit.HOURS);
    }

    /**
     * 调用企业微信应用发送消息
     * @param sendMsgData
     * @param accessToken
     * @return
     */
    public SendMsgRes sendLateMsg(SendMsgData sendMsgData,String accessToken){
        SendMsgRes sendMsgRes = null;
        try {
            sendMsgRes = wxServiceImpl.sendLateMsg(sendMsgData, accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sendMsgRes;
    }

}
