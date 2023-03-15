package ru.malakhov.telegrambot.bot.enums;

public enum BotCommand {
    START("/start"),
    REG("/registration"),
    CHANGE("/change"),
    CONFIRM("/confirm"),
    HELP("/help");

    private final String commandName;

    BotCommand(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}