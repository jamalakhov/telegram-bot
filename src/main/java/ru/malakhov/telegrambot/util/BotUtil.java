package ru.malakhov.telegrambot.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class BotUtil {

    public static SendMessage createDefaultSendMessage(long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}