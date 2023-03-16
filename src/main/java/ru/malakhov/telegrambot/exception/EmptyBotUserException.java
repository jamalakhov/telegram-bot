package ru.malakhov.telegrambot.exception;

public class EmptyBotUserException extends Exception {

    public EmptyBotUserException(String message) {
        super(message);
    }
}