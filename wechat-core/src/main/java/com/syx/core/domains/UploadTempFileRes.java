package com.syx.core.domains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/3/22
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadTempFileRes {

    @JsonProperty(value = "errcode")
    private int errcode;
    @JsonProperty(value = "errmsg")
    private String errmsg;
    @JsonProperty(value = "type")
    private String type;
    @JsonProperty(value = "media_id")
    private String media_id;
    @JsonProperty(value = "created_at")
    private String created_at;
}
