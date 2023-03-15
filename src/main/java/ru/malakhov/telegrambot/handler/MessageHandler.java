package ru.malakhov.telegrambot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.malakhov.telegrambot.bot.TelegramBot;
import ru.malakhov.telegrambot.storage.BotStorage;

@Component
public class MessageHandler {
    private TelegramBot bot;
    private final BotStorage botStorage;
    private final TextHandler textHandler;
    private final CommandHandler commandHandler;


    public MessageHandler(CommandHandler commandHandler,
                          BotStorage botStorage, TextHandler textHandler) {
        this.commandHandler = commandHandler;
        this.botStorage = botStorage;
        this.textHandler = textHandler;
    }

    public void init(TelegramBot bot) {
        this.bot = bot;
    }

    public void handling(Update update) {
        SendMessage message;
        var inputText = update.getMessage().getText().split(" ")[0];

        if (inputText.startsWith("/")) {
            message = commandHandler.handling(update, botStorage);
        } else {
            message = textHandler.handling(update, botStorage);
        }

        bot.sendingMessage(message);
    }
}