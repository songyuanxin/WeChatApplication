package com.syx.core.domains;

import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/3/22
 **/
@Data
public class SendEftsExcelData {
    private String touser;
    private String msgtype;
    private String agentid;
    private EftsMsgData file;
    private int safe;
    private int enable_duplicate_check;
    private int duplicate_check_interval;
}
