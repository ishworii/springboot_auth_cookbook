package com.ishwor.authcookbook.none;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.ishwor.authcookbook")
@EnableJpaRepositories(basePackages = "com.ishwor.authcookbook.common")
@EntityScan(basePackages = "com.ishwor.authcookbook.common")
public class AppNoneApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppNoneApplication.class, args);
    }
}
