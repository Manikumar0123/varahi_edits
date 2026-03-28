package com.varahiedits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class VarahiEditsApplication {
    public static void main(String[] args) {
        SpringApplication.run(VarahiEditsApplication.class, args);
    }
}
