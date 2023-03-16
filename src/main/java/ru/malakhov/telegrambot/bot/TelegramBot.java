package ru.malakhov.telegrambot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.malakhov.telegrambot.handler.MainHandler;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;

    private final MainHandler mainHandler;

    public TelegramBot(@Value("${bot.token}") String botToken,
                       MainHandler mainHandler) {
        super(botToken);
        this.mainHandler = mainHandler;
    }

    @PostConstruct
    private void init() {
        mainHandler.init(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null) {
            if (update.hasMessage() && update.getMessage().hasText() || update.hasCallbackQuery()) {
                mainHandler.handling(update);
            } else {
                log.error("Update is null [" + LocalDateTime.now() + "]");
            }
        }
    }

    public void sendingMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}