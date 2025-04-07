package com.auctions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuctionsApplication {

    public static void main(String[] args) {

        SpringApplication.run(AuctionsApplication.class, args);
    }
}