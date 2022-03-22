package com.syx.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan(basePackages = {"com.syx.system.mapper"})
public class WechatSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatSystemApplication.class, args);
        System.out.println("企业微信应用服务启动成功！");
    }

}
