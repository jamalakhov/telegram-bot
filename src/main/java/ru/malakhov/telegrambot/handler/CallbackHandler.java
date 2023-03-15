package ru.malakhov.telegrambot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.malakhov.telegrambot.bot.TelegramBot;
import ru.malakhov.telegrambot.storage.BotStorage;

@Slf4j
@Component
public class CallbackHandler {
    private TelegramBot bot;
    private final BotStorage botStorage;
    private final TextHandler textHandler;
    private final CommandHandler commandHandler;

    public CallbackHandler(BotStorage botStorage,
                           TextHandler textHandler,
                           CommandHandler commandHandler) {
        this.botStorage = botStorage;
        this.textHandler = textHandler;
        this.commandHandler = commandHandler;
    }

    public void init(TelegramBot bot) {
        this.bot = bot;
    }

    public void handling(Update update) {
        var callback = update.getCallbackQuery();
        var callbackData = callback.getData();

        SendMessage message;

        if (callbackData.startsWith("/")) {
            message = commandHandler.handling(update, botStorage);
        } else {
            message = textHandler.handling(update, botStorage);
        }

        bot.sendingMessage(message);
    }
}