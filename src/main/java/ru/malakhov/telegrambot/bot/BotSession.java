package ru.malakhov.telegrambot.bot;

import lombok.Builder;
import lombok.Data;
import ru.malakhov.telegrambot.bot.enums.BotState;
import ru.malakhov.telegrambot.entity.BotUser;

import java.time.LocalDateTime;

@Data
@Builder
public class BotSession {
    private BotState state;
    private BotUser botUser;
    private LocalDateTime dateCreate;
}