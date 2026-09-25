package com.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DevMission2Application {

    public static void main(String[] args) {
        SpringApplication.run(DevMission2Application.class, args);
    }

}
