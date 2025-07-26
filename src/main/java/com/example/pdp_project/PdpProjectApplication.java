package com.example.pdp_project;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PdpProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdpProjectApplication.class, args);
    }

    @Bean
    public TelegramBot telegramBot() {
        String token = "";
        return new TelegramBot(token);
    }
}
