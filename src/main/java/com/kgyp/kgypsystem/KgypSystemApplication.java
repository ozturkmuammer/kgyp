package com.kgyp.kgypsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KgypSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(KgypSystemApplication.class, args);
    }

}
