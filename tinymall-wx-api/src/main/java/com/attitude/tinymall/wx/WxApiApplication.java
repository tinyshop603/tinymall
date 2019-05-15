package com.attitude.tinymall.wx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={"com.attitude.tinymall"})
@EnableScheduling
@EnableFeignClients(basePackages = "com.attitude.tinymall")
@EnableHystrix
public class WxApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(WxApiApplication.class, args);
    }

}
