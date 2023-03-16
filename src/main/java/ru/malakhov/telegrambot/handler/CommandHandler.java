package ru.malakhov.telegrambot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.malakhov.telegrambot.bot.BotSession;
import ru.malakhov.telegrambot.bot.enums.BotState;
import ru.malakhov.telegrambot.exception.EmptyBotUserException;
import ru.malakhov.telegrambot.kyeboard.Keyboards;
import ru.malakhov.telegrambot.storage.BotStorage;
import ru.malakhov.telegrambot.util.BotUtil;

import java.time.LocalDateTime;

import static ru.malakhov.telegrambot.bot.enums.BotState.*;

@Slf4j
@Component
public class CommandHandler {
    private final BotStorage botStorage;

    public CommandHandler(BotStorage botStorage) {
        this.botStorage = botStorage;
    }

    public SendMessage handling(BotSession botSession) {
        var command = botSession.getBotCommand().trim().split(" ")[0];

        var result = SendMessage.builder()
                .chatId(botSession.getId())
                .text("Неизвестная ошибка. Попробуйте снова.")
                .build();

        try {
            result = switch (command) {
                case "/start" -> startCommand(botSession);
                case "/help" -> helpCommand(botSession);
                case "/confirm" -> confirmCommand(botSession);
                case "/change" -> changeCommand(botSession);
                case "/cancel" -> cancelCommand(botSession);
                case "/registration" -> registrationCommand(botSession);
                default -> errorCommand(botSession);
            };
        } catch (EmptyBotUserException e) {
            log.error(e.toString());
        }

        return result;
    }


    private SendMessage startCommand(BotSession session) {
        var message = "";
        var response = BotUtil.createDefaultSendMessage(session.getId(), message);

        if (session.getBotUser().isRegistration()) {
            message = session.getBotUser().getFirstName() + ", рад вас видеть!";
            response.setReplyMarkup(Keyboards.menuKeyboard());

        } else {
            message = "Чтобы воспользоваться ботом пройдите регистрацию";
            response.setReplyMarkup(Keyboards.registrationKeyboard());
        }

        response.setText(message);
        return response;
    }

    private SendMessage registrationCommand(BotSession session) throws EmptyBotUserException {
        var message = "";
        var response = BotUtil.createDefaultSendMessage(session.getId(), message);

        if (session.getBotUser().isRegistration()) {
            message = "Вы уже зарегистрированы";
        } else {
            session.setState(BotState.ENTER_EMAIL);
            botStorage.saveSession(session);
            message = "Введите свой email";
        }

        response.setText(message);
        return response;
    }

    private SendMessage helpCommand(BotSession session) {
        var message = """
                Список доступных команд:
                                
                /start - начать работу с ботом
                /help - показать список команд
                /registration - регистрация в боте"
                                
                Чтобы воспользоваться полным функционалом
                бота пройдите регистрацию.
                """;

        return BotUtil.createDefaultSendMessage(session.getId(), message);
    }

    private SendMessage cancelCommand(BotSession session) throws EmptyBotUserException {
        var message = "Действие отменено";

        if (session.getBotUser().isRegistration()) {
            session.setState(MAIN);
        } else {
            session.setState(START);
        }

        botStorage.saveSession(session);
        return BotUtil.createDefaultSendMessage(session.getId(), message);
    }

    private SendMessage changeCommand(BotSession session) throws EmptyBotUserException {
        var message = "Введите свой email";
        var botUser = session.getBotUser();

        botUser.setPhoneNumber(null);
        botUser.setEmail(null);
        botUser.setRegistration(false);
        botStorage.saveBotUser(botUser);

        session.setState(ENTER_EMAIL);
        session.setBotUser(botUser);
        botStorage.saveSession(session);

        return BotUtil.createDefaultSendMessage(session.getId(), message);
    }

    private SendMessage confirmCommand(BotSession session) throws EmptyBotUserException {
        var message = "Ваши данные успешно добавлены";
        var botUser = session.getBotUser();

        botUser.setRegistration(true);
        botStorage.saveBotUser(botUser);

        session.setState(MAIN);
        session.setBotUser(botUser);
        session.setDateCreate(LocalDateTime.now());
        botStorage.saveSession(session);

        var response = BotUtil.createDefaultSendMessage(session.getId(), message);
        response.setReplyMarkup(Keyboards.menuKeyboard());

        return response;
    }

    private SendMessage errorCommand(BotSession session) {
        var message = "Неизвестная команда. Введите /help, чтобы увидеть список доступных команд.";
        return BotUtil.createDefaultSendMessage(session.getId(), message);
    }
}