package ru.malakhov.telegrambot.util;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.malakhov.telegrambot.bot.BotSession;
import ru.malakhov.telegrambot.bot.enums.BotState;
import ru.malakhov.telegrambot.entity.BotUser;
import ru.malakhov.telegrambot.storage.BotStorage;

import java.time.LocalDateTime;

public class BotUtil {
    public static User getUser(Update update) {
        if (update.hasMessage()) return update.getMessage().getFrom();
        if (update.hasCallbackQuery()) return update.getCallbackQuery().getFrom();
        return null;
    }

    public static Chat getChat(Update update) {
        if (update.hasMessage()) return update.getMessage().getChat();
        if (update.hasCallbackQuery()) return update.getCallbackQuery().getMessage().getChat();
        return null;
    }

    public static String getText(Update update) {
        if (update.hasMessage()) return update.getMessage().getText();
        if (update.hasCallbackQuery()) return update.getCallbackQuery().getData();
        return null;
    }

    public static BotUser botUserValidate(Update update, BotStorage botStorage) {
        var user = getUser(update);

        if (botStorage.containsBotUser(user.getId())) return botStorage.getBotUsers(user.getId());

        var botUser = BotUser.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .registration(false)
                .dateCreate(LocalDateTime.now())
                .build();

        botStorage.saveBotUser(botUser);
        return botUser;
    }

    public static BotSession sessionValidate(Update update, BotStorage botStorage) {
        long userId = getUser(update).getId();

        if (botStorage.containsSession(userId)) {
            var session = botStorage.getSession(userId);

            if (session.getBotUser() != null) return session;

            var botUser = botUserValidate(update, botStorage);
            session.setBotUser(botUser);
            botStorage.saveSession(session);

            return session;
        } else {
            var botUser = botUserValidate(update, botStorage);
            var session = BotSession.builder()
                    .state(BotState.START)
                    .botUser(botUser)
                    .dateCreate(LocalDateTime.now())
                    .build();

            botStorage.saveSession(session);

            return session;
        }
    }
}
