package com.example.pdp_project.service;

import com.example.pdp_project.entity.Trip;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InputFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BotService {
    private final TelegramBot bot = new TelegramBot("7972181719:AAGXKFoV8eUMiC-bhmgP8BDM5jcjxnAjqho");;

    public void MessageService(Message message) {
        Long userId = message.from().id();


    }

    public void CallbackQuery(CallbackQuery callbackQuery) {
        Long userId = callbackQuery.from().id();

    }

    public void addTripForChannel(Trip trip){
        if (trip.getPhoto() != null && trip.getPhoto().getContent() != null) {
                String caption = String.format("""
                🌍 Yangi Trip Qo‘shildi!
                📌 Title: %s
                📖 Description: %s
                🗺 Country: %s
                ⭐ Rating: %.1f
                💰 Price: $%.2f
                """,
                        trip.getTitle(),
                        trip.getDescription(),
                        trip.getCountry(),
                        trip.getRating(),
                        trip.getPrice());

                byte[] content = trip.getPhoto().getContent();


                SendPhoto sendPhoto = new SendPhoto("@pdp_exam_project", content)
                        .caption(caption);
                bot.execute(sendPhoto);

                System.out.println("Trip rasmi bilan caption yuborildi ✅");

        } else {
            // Attachment yo‘q bo‘lsa faqat text yuboramiz
            String message = String.format("""
            🌍 Yangi Trip Qo‘shildi!
            📌 Title: %s
            📖 Description: %s
            🗺 Country: %s
            ⭐ Rating: %.1f
            💰 Price: $%.2f
            """,
                    trip.getTitle(),
                    trip.getDescription(),
                    trip.getCountry(),
                    trip.getRating(),
                    trip.getPrice());

            bot.execute(new SendMessage("@pdp_exam_project", message));
        }
    }
}
