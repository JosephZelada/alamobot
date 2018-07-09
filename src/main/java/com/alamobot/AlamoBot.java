package com.alamobot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("com.alamobot")
@ComponentScan("com.alamobot")
@EnableScheduling
public class AlamoBot {

    public static void main(String[] args) {
        SpringApplication.run(AlamoBot.class, args);
    }

}
