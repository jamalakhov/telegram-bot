package ru.malakhov.telegrambot.storage;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.malakhov.telegrambot.bot.BotSession;
import ru.malakhov.telegrambot.entity.BotUser;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class BotStorage {
    private static Map<Long, BotSession> botSessions = new HashMap<>();
    private static Map<Long, BotUser> botUsers = new HashMap<>();

    public BotSession getSession(long userId) {
        return botSessions.getOrDefault(userId, null);
    }

    public void saveSession(BotSession session) {
        botSessions.put(session.getBotUser().getId(), session);
    }

    public void removeSession(long userId) {
        if (botSessions.containsKey(userId)) {
            botSessions.remove(userId);
        }
    }


    public boolean containsSession(long userId) {
        return botSessions.containsKey(userId);
    }

    public BotUser getBotUsers(long userId) {
        return botUsers.getOrDefault(userId, null);
    }

    public void saveBotUser(BotUser botUser) {
        botUsers.put(botUser.getId(), botUser);
    }

    public boolean containsBotUser(long userId) {
        return botUsers.containsKey(userId);
    }
}