package ru.malakhov.telegrambot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.malakhov.telegrambot.bot.enums.BotState;
import ru.malakhov.telegrambot.storage.BotStorage;
import ru.malakhov.telegrambot.util.BotUtil;

import java.time.LocalDateTime;

import static ru.malakhov.telegrambot.bot.enums.BotState.*;

@Component
public class CommandHandler {
    private BotStorage botStorage;

    public SendMessage handling(Update update, BotStorage botStorage) {
        this.botStorage = botStorage;

        var text = BotUtil.getText(update);

        return switch (text) {
            case "/start" -> startCommand(update);
            case "/help" -> helpCommand(update);
            case "/confirm" -> confirmCommand(update);
            case "/change" -> changeCommand(update);
            case "/cancel" -> cancelCommand(update);
            case "/registration" -> registrationCommand(update);
            default -> errorCommand(update);
        };
    }

    private SendMessage cancelCommand(Update update) {
        var user = BotUtil.getUser(update);
        var session = botStorage.getSession(user.getId());
        var botUser = session.getBotUser();

        if (botUser.isRegistration()) {
            session.setState(MAIN);
        } else {
            session.setState(START);
        }

        botStorage.saveSession(session);

        return SendMessage.builder()
                .chatId(botUser.getId())
                .text("Действие отменено")
                .build();
    }

    private SendMessage changeCommand(Update update) {
        var user = BotUtil.getUser(update);
        var session = botStorage.getSession(user.getId());
        var botUser = session.getBotUser();

        botUser.setPhoneNumber(null);
        botUser.setEmail(null);
        botUser.setRegistration(false);
        botStorage.saveBotUser(botUser);

        session.setState(ENTER_EMAIL);
        session.setBotUser(botUser);
        botStorage.saveSession(session);

        return SendMessage.builder()
                .chatId(botUser.getId())
                .text("Введите свой email")
                .build();
    }

    private SendMessage confirmCommand(Update update) {
        var user = BotUtil.getUser(update);
        var session = botStorage.getSession(user.getId());
        var botUser = session.getBotUser();

        botUser.setRegistration(true);
        botStorage.saveBotUser(botUser);

        session.setState(BotState.MAIN);
        session.setDateCreate(LocalDateTime.now());
        botStorage.saveSession(session);
        //А BotUser сохраняем в базу

        return SendMessage.builder()
                .chatId(botUser.getId())
                .text("Ваши данные успешно добавлены")
                .build();
    }

    private SendMessage startCommand(Update update) {
        var chat = BotUtil.getChat(update);
        var user = BotUtil.getUser(update);
        var response = SendMessage.builder()
                .chatId(chat.getId())
                .text("")
                .build();

        if (botStorage.containsBotUser(user.getId())) {
            BotUtil.sessionValidate(update, botStorage);

            response.setText(user.getFirstName() + ", с возвращением!");
            return response;
        }

        var botUser = BotUtil.botUserValidate(update, botStorage);
        botStorage.removeSession(botUser.getId());
        BotUtil.sessionValidate(update, botStorage);

        response.setText(user.getFirstName() + ", привет! Я телеграмм бот, твой персональный помощник.");

        return response;
    }

    private SendMessage registrationCommand(Update update) {
        User user = BotUtil.getUser(update);

        if (botStorage.containsBotUser(user.getId()) && botStorage.getBotUsers(user.getId()).isRegistration()) {
            return SendMessage.builder()
                    .chatId(user.getId())
                    .text("Вы уже зарегистрированы")
                    .build();
        }

        var response = startCommand(update);
        var session = botStorage.getSession(user.getId());

        session.setState(BotState.ENTER_EMAIL);
        botStorage.saveSession(session);

        response.setText("Введите свой email:");
        return response;
    }

    private SendMessage helpCommand(Update update) {
        var text = """
                Список доступных команд:
                                
                /start - начать работу с ботом
                /help - показать список команд
                /registration - регистрация в боте"
                                
                Чтобы воспользоваться полным функционалом
                бота пройдите регистрацию.
                """;

        return defaultCommand(update, text);
    }

    private SendMessage errorCommand(Update update) {
        var text = "Неизвестная команда. Введите /help, чтобы увидеть список доступных команд.";

        return defaultCommand(update, text);
    }

    private SendMessage defaultCommand(Update update, String text) {
        var response = startCommand(update);
        response.setText(text);

        return response;
    }
}