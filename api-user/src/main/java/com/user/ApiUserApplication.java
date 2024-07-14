package com.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.user", "com.storage.config"})
public class ApiUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiUserApplication.class, args);
    }

}
