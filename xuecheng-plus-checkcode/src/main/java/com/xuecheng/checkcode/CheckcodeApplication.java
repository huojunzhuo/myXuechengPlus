package com.xuecheng.checkcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class}) //显示的禁用DataSource
public class CheckcodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckcodeApplication.class, args);
    }

}
