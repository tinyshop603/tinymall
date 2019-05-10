package com.attitude.tinymall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {
        "com.attitude.tinymall",
        "com.attitude.tinymall.core",
        "com.attitude.tinymall.db",
        "com.attitude.tinymall.os",
        "com.attitude.tinymall.wx",
        "com.attitude.tinymall.admin"})
@MapperScan("com.attitude.tinymall.db.dao")
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}
