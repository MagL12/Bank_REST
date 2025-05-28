package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.bankcards")
public class BankcardsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankcardsApplication.class, args);
    }
}