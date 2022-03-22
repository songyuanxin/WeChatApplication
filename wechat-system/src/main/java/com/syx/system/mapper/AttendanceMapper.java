package com.syx.system.mapper;

import com.syx.core.domains.WMsgLog;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/3/2
 **/
public interface AttendanceMapper {
    List<WMsgLog> getMsgLog();

    int setSendMsgTime(String pernr,Timestamp timestampNow);

}
