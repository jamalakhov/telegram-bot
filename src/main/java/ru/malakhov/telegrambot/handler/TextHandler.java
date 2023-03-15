package ru.malakhov.telegrambot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.malakhov.telegrambot.bot.BotSession;
import ru.malakhov.telegrambot.bot.enums.BotState;
import ru.malakhov.telegrambot.kyeboard.Keyboards;
import ru.malakhov.telegrambot.storage.BotStorage;
import ru.malakhov.telegrambot.util.BotUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.malakhov.telegrambot.bot.enums.BotState.*;

@Component
public class TextHandler {
    private BotStorage botStorage;

    public SendMessage handling(Update update, BotStorage botStorage) {
        this.botStorage = botStorage;

        var message = update.getMessage();
        var user = message.getFrom();
        var text = message.getText();

        var session = BotUtil.sessionValidate(update, botStorage);

        return switch (session.getState()) {
            case MAIN -> mainMessage(update, session);
            case START -> startMessage(update, session);
            case ENTER_EMAIL -> emailMessage(update, session);
            case ENTER_PHONE_NUMBER -> phoneMessage(update, session);
            case CONFIRM -> confirmMessage(update, session);
        };
    }

    private SendMessage mainMessage(Update update, BotSession session) {
        return defaultMessage(update, update.getMessage().getText().toUpperCase());
    }

    private SendMessage startMessage(Update update, BotSession session) {
        var text = "Чтобы использовать мой фунционал наполную, пройди регистрацию.";

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        var rows = new ArrayList<List<InlineKeyboardButton>>();
        var row = new ArrayList<InlineKeyboardButton>();
        var button = new InlineKeyboardButton("Регистрация");
        button.setCallbackData("/registration");

        row.add(button);
        rows.add(row);
        keyboard.setKeyboard(rows);

        var response = defaultMessage(update, text);
        response.setReplyMarkup(keyboard);

        return response;
    }

    private SendMessage emailMessage(Update update, BotSession session) {
        var text = "Введите свой номер телефона";
        var message = update.getMessage();
        var botUser = session.getBotUser();

        botUser.setEmail(message.getText());
        botStorage.saveBotUser(botUser);

        session.setBotUser(botUser);
        session.setState(ENTER_PHONE_NUMBER);
        botStorage.saveSession(session);

        return defaultMessage(update, text);
    }

    private SendMessage phoneMessage(Update update, BotSession session) {
        var header = "Подтвердите ваши данные:\n\n";
        var footer = "\n\n*Нажмите одну из кнопок или введите Да/Нет";
        var message = update.getMessage();
        var botUser = session.getBotUser();

        botUser.setPhoneNumber(message.getText());
        botStorage.saveBotUser(botUser);

        session.setBotUser(botUser);
        session.setState(CONFIRM);
        botStorage.saveSession(session);

        var keyboard = Keyboards.confirmKeyboard();
        var response = defaultMessage(update, header + botUser + footer);
        response.setReplyMarkup(keyboard);
        return response;
    }

    private SendMessage confirmMessage(Update update, BotSession session) {
        var header = "Неизвестный текст или команда\n\nПодтвердите ваши данные:\n\n";

        var botUser = session.getBotUser();

        var keyboard = Keyboards.confirmKeyboard();
        var response = defaultMessage(update, header + botUser);
        response.setReplyMarkup(keyboard);

        return response;
    }

    private SendMessage defaultMessage(Update update, String text) {
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(text)
                .build();
    }
}