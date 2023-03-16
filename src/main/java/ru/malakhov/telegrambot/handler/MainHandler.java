package ru.malakhov.telegrambot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.malakhov.telegrambot.bot.BotSession;
import ru.malakhov.telegrambot.bot.TelegramBot;
import ru.malakhov.telegrambot.entity.BotUser;
import ru.malakhov.telegrambot.storage.BotStorage;

import java.time.LocalDateTime;
import java.util.Optional;

import static ru.malakhov.telegrambot.bot.enums.BotState.MAIN;
import static ru.malakhov.telegrambot.bot.enums.BotState.START;

@Slf4j
@Component
public class MainHandler {
    private final BotStorage botStorage;
    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;

    public MainHandler(BotStorage botStorage,
                       MessageHandler messageHandler,
                       CallbackHandler callbackHandler) {
        this.botStorage = botStorage;
        this.messageHandler = messageHandler;
        this.callbackHandler = callbackHandler;
    }

    public void init(TelegramBot bot) {
        messageHandler.init(bot);
        callbackHandler.init(bot);
    }

    public void handling(Update update) {
        User user;

        if (update.hasMessage()) {
            user = update.getMessage().getFrom();
            var session = getOrCreateBotSession(user);
            session.setMessage(update.getMessage());

            messageHandler.handling(session);
        } else if (update.hasCallbackQuery()) {

            user = update.getCallbackQuery().getFrom();
            var session = getOrCreateBotSession(user);
            session.setMessage(update.getCallbackQuery().getMessage());
            session.setCallbackData(update.getCallbackQuery().getData());

            callbackHandler.handling(session);
        } else {
            log.error("Unknown state: " + update);
        }
    }

    private BotSession getOrCreateBotSession(User user) {
        Optional<BotSession> optionalBotSession = botStorage.getSession(user.getId());

        return optionalBotSession.orElseGet(() -> this.createBotSession(user));
    }

    private BotSession createBotSession(User user) {
        var botUser = this.getBotUser(user);
        var session = BotSession.builder()
                .id(user.getId())
                .state(START)
                .botUser(botUser)
                .message(null)
                .callbackData(null)
                .botCommand(null)
                .dateCreate(LocalDateTime.now())
                .build();

        if (botUser.isRegistration()) session.setState(MAIN);

        botStorage.saveSession(session);
        return session;
    }

    private BotUser getBotUser(User user) {
        Optional<BotUser> optionalBotUser = botStorage.getBotUser(user.getId());

        return optionalBotUser.orElseGet(() -> createBotUser(user));
    }

    private BotUser createBotUser(User user) {
        var botUser = BotUser.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .email(null)
                .phoneNumber(null)
                .registration(false)
                .dateCreate(LocalDateTime.now())
                .build();

        botStorage.saveBotUser(botUser);
        return botUser;
    }
}
