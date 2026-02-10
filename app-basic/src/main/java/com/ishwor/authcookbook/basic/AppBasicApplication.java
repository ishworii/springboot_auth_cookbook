package com.ishwor.authcookbook.basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication(scanBasePackages = "com.ishwor.authcookbook")
@EnableJpaRepositories(basePackages = "com.ishwor.authcookbook.common")
@EntityScan(basePackages = "com.ishwor.authcookbook.common")
public class AppBasicApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppBasicApplication.class, args);
    }
}
