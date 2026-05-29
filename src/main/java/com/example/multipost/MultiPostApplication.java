package com.example.multipost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MultiPostApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiPostApplication.class, args);
    }

}
