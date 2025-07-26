package com.example.pdp_project.config.bot;

import com.example.pdp_project.service.BotService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotRunner implements CommandLineRunner {

    private final TelegramBot telegramBot = new TelegramBot("7972181719:AAGXKFoV8eUMiC-bhmgP8BDM5jcjxnAjqho");
    private final BotService botService;

    @Override
    public void run(String... args) throws Exception {
        telegramBot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                Message message = update.message();
                CallbackQuery callbackQuery = update.callbackQuery();

                if (message != null) {
                    botService.MessageService(message);
                }
                if (callbackQuery != null) {
                    botService.CallbackQuery(callbackQuery);
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

    }
}
