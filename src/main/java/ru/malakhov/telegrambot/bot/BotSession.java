package ru.malakhov.telegrambot.bot;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.malakhov.telegrambot.bot.enums.BotState;
import ru.malakhov.telegrambot.entity.BotUser;

import java.time.LocalDateTime;

@Data
@Builder
public class BotSession {
    private Long id;
    private BotState state;
    private BotUser botUser;
    private Message message;
    private String callbackData;
    private String botCommand;
    private LocalDateTime dateCreate;
}