package com.syx.system.controller;

import com.syx.core.domains.AjaxResult;
import com.syx.core.domains.SendMsgRes;
import com.syx.system.service.EftsExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;

/**
 * @author 宋远欣
 * @date 2022/3/22
 **/
@RestController
@RequestMapping("/eftsExcel")
public class EftsExcelController {

    @Autowired
    private EftsExcelService eftsExcelService;

    /**
     * 发送迟到信息
     * @return
     */
    @PostMapping(path = "/uploadTempFile")
    public AjaxResult uploadTempFile(){
        
        File excel = new File("D:\\test\\英克发票回传数据同步报税系统.xlsx");
        SendMsgRes sendMsgRes = eftsExcelService.sendEftsMsg(excel);

        if (sendMsgRes.getErrcode() != 0){
            return AjaxResult.error("上传临时素材失败",sendMsgRes.getErrmsg());
        }
        return AjaxResult.success("上传临时素材成功",sendMsgRes.getErrmsg());
    }

}
