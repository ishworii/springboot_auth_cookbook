package com.ishwor.authcookbook.none;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ishwor.authcookbook")
public class AppNoneApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppNoneApplication.class, args);
    }
}
