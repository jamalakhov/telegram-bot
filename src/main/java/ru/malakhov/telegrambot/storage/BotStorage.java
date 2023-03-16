package ru.malakhov.telegrambot.storage;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.malakhov.telegrambot.bot.BotSession;
import ru.malakhov.telegrambot.entity.BotUser;
import ru.malakhov.telegrambot.exception.EmptyBotUserException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@Component
public class BotStorage {
    private static Map<Long, BotSession> botSessions = new HashMap<>();
    private static Map<Long, BotUser> botUsers = new HashMap<>();

    public Optional<BotSession> getSession(long userId) {
        if (botSessions.containsKey(userId)) return Optional.of(botSessions.get(userId));

        return Optional.empty();
    }

    public void saveSession(BotSession session) throws EmptyBotUserException {
        if(session.getBotUser() == null) throw new EmptyBotUserException("BotUser is null!");
        botSessions.put(session.getBotUser().getId(), session);
    }

    public void removeSession(long userId) {
        botSessions.remove(userId);
    }

    public Optional<BotUser> getBotUser(long userId) {
        if (botUsers.containsKey(userId)) return Optional.of(botUsers.get(userId));

        return Optional.empty();
    }

    public void saveBotUser(BotUser botUser) {
        botUsers.put(botUser.getId(), botUser);
    }
}