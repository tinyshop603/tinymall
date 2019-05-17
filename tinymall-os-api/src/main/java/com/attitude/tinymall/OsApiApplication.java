package com.attitude.tinymall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.attitude.tinymall"})
public class OsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OsApiApplication.class, args);
    }

}
