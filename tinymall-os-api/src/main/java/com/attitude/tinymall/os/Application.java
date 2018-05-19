package com.attitude.tinymall.os;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.attitude.tinymall.core", "com.attitude.tinymall.os","com.attitude.tinymall.db"})
@MapperScan("com.attitude.tinymall.db.dao")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}