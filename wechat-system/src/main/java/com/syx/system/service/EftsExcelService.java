package com.syx.system.service;

import com.syx.core.domains.SendMsgRes;
import com.syx.core.domains.UploadTempFileRes;

import java.io.File;

/**
 * @author 宋远欣
 * @date 2022/3/22
 **/
public interface EftsExcelService {

    /**
     * 上传临时素材
     * @param file
     * @return
     */
    SendMsgRes sendEftsMsg(File file);
}
