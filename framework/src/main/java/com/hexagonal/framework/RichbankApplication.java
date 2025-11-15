package com.hexagonal.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.hexagonal.framework",
    "com.hexagonal.application"
})
@EnableCaching
@EnableJpaRepositories(basePackages = "com.hexagonal.framework.adapter.output.persistence.h2")
@EnableMongoRepositories(basePackages = "com.hexagonal.framework.adapter.output.persistence.mongodb")
public class RichbankApplication {

    public static void main(String[] args) {
        SpringApplication.run(RichbankApplication.class, args);
    }
}

