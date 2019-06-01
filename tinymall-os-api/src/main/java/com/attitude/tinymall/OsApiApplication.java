package com.attitude.tinymall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages={"com.attitude.tinymall"})
@EnableFeignClients(basePackages = "com.attitude.tinymall")
public class OsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OsApiApplication.class, args);
    }

}
