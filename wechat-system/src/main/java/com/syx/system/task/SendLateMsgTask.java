package com.syx.system.task;

import com.syx.core.domains.AjaxResult;
import com.syx.core.domains.SendMsgRes;
import com.syx.system.service.AttendanceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author 宋远欣
 * @date 2022/3/2
 **/
@Log4j2
@Configuration
@EnableScheduling
public class SendLateMsgTask {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 发送迟到信息
     * @return
     */
//    @Scheduled(cron = "0 0/30 10-19 * * ?")
    public AjaxResult sendLateMsg(){
        SendMsgRes sendMsgRes = attendanceService.sendLateMsg();

        if (sendMsgRes.getErrcode() != 0){
            return AjaxResult.error("发送信息失败",sendMsgRes.getErrmsg());
        }
        return AjaxResult.success("发送信息成功",sendMsgRes.getErrmsg());
    }
}
