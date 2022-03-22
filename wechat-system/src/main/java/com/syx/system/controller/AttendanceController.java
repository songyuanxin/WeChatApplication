package com.syx.system.controller;

import com.syx.core.domains.AjaxResult;
import com.syx.core.domains.SendMsgRes;
import com.syx.system.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 宋远欣
 * @date 2022/3/1
 **/
@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 发送迟到信息
     * @return
     */
    @GetMapping(path = "/sendLateMsg")
    public AjaxResult sendLateMsg(){
        SendMsgRes sendMsgRes = attendanceService.sendLateMsg();
        if (sendMsgRes.getErrcode() != 0){
            return AjaxResult.error("发送信息失败",sendMsgRes.getErrmsg());
        }
        return AjaxResult.success("发送信息成功",sendMsgRes.getErrmsg());
    }


}
