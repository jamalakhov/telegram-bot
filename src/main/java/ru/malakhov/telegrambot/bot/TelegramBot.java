package ru.malakhov.telegrambot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.malakhov.telegrambot.handler.CallbackHandler;
import ru.malakhov.telegrambot.handler.MessageHandler;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;

    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;

    public TelegramBot(@Value("${bot.token}") String botToken,
                       MessageHandler messageHandler,
                       CallbackHandler callbackHandler) {
        super(botToken);
        this.messageHandler = messageHandler;
        this.callbackHandler = callbackHandler;
    }

    @PostConstruct
    private void init() {
        messageHandler.init(this);
        callbackHandler.init(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null) {
            if (update.hasMessage()) {
                var message = update.getMessage();

                if (message.hasText()) {
                    messageHandler.handling(update);
                }
            }

            if (update.hasCallbackQuery()) {
                callbackHandler.handling(update);
            }
        } else {
            log.error("Update is null [" + LocalDateTime.now() + "]");
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