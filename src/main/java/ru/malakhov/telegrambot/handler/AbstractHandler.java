package ru.malakhov.telegrambot.handler;

import ru.malakhov.telegrambot.bot.BotSession;
import ru.malakhov.telegrambot.bot.TelegramBot;

public abstract class AbstractHandler {
    protected TelegramBot bot;
    protected final TextHandler textHandler;
    protected final CommandHandler commandHandler;

    protected AbstractHandler(TextHandler textHandler,
                              CommandHandler commandHandler) {
        this.textHandler = textHandler;
        this.commandHandler = commandHandler;
    }

    protected void init(TelegramBot bot) {
        this.bot = bot;
    }

    public abstract void handling(BotSession botSession);
}