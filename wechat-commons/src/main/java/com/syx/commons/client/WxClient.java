package com.syx.commons.client;

import com.alibaba.fastjson.JSONObject;
import com.syx.core.domains.SendMsgData;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 宋远欣
 * @date 2022/3/1
 **/
@Service
public class WxClient {

    private static final String HOST = "qyapi.weixin.qq.com";
    private static final Pattern PATTERN = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");

    public String executeGet(Api api, Map<String, Object> params) throws IOException {
        String url = "https://" + HOST + api.Url + "?" + getQueryString(params);
        HttpClient httpClient = new HttpClient();
        GetMethod method = new GetMethod(url.replace(" ", "%20"));
        httpClient.executeMethod(method);
        String responseBodyAsString = method.getResponseBodyAsString();
        return unicodeToString(responseBodyAsString);
    }

    public String executePost(Api api, SendMsgData sendMsgData, Map<String, Object> params) throws IOException {
        String url = "https://" + HOST + api.Url + "?" + getQueryString(params);
        HttpClient httpClient = new HttpClient();
        PostMethod method = new PostMethod(url.replace(" ", "%20"));
        RequestEntity requestEntity = new StringRequestEntity(JSONObject.toJSON(sendMsgData).toString(), "application/json", "UTF-8");
        method.setRequestEntity(requestEntity);
        httpClient.executeMethod(method);
        String responseBodyAsString = method.getResponseBodyAsString();
        return unicodeToString(responseBodyAsString);
    }

    private String getQueryString(Map<String, Object> params) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (Map.Entry<String, Object> i : params.entrySet()) {
            if (index > 0) {
                builder.append("&");
            }
            builder.append(i.getKey()).append("=").append(i.getValue());
            index++;
        }
        return builder.toString();
    }

    private static String unicodeToString(String unicodeStr) {
        Matcher matcher = PATTERN.matcher(unicodeStr);
        char ch;
        while (matcher.find()) {
            // 捕获组按开括号'('从左到右编号（从1开始），以(A(B(C)))为例，group(1)表示(A(B(C))，group(2)表示(B(C))，group(3)表示(C)
            // group(2)表示第二个捕获组，即(\p{XDigit}{4})
            // Integer.parseInt(str, 16)把16进制的数字字符串转化为10进制，比如Integer.parseInt("16", 16) = 22
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            // 把第一个捕获组，即形如\\u6211这样的字符串替换成中文
            unicodeStr = unicodeStr.replace(matcher.group(1), ch + "");
        }
        return unicodeStr;
    }

    public enum Api {
        //获取应用token
        ACCESS_TOKEN_GET("/cgi-bin/gettoken"),
        //调企业微信应用发送消息
        SEND_MSG("/cgi-bin/message/send");

        public final String Url;

        Api(String url) {
            Url = url;
        }
    }
}
