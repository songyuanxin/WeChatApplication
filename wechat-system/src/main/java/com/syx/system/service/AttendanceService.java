package com.syx.system.service;

import com.syx.core.domains.SendMsgRes;

/**
 * @author 宋远欣
 * @date 2022/3/1
 **/
public interface AttendanceService {

    /**
     * 发送迟到提醒
     * @return
     */
    SendMsgRes sendLateMsg();
}
