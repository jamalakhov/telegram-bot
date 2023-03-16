package ru.malakhov.telegrambot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.malakhov.telegrambot.bot.BotSession;
import ru.malakhov.telegrambot.exception.EmptyBotUserException;
import ru.malakhov.telegrambot.kyeboard.Keyboards;
import ru.malakhov.telegrambot.storage.BotStorage;
import ru.malakhov.telegrambot.util.BotUtil;

import static ru.malakhov.telegrambot.bot.enums.BotState.CONFIRM;
import static ru.malakhov.telegrambot.bot.enums.BotState.ENTER_PHONE_NUMBER;

@Slf4j
@Component
public class TextHandler {
    private final BotStorage botStorage;

    public TextHandler(BotStorage botStorage) {
        this.botStorage = botStorage;
    }

    public SendMessage handling(BotSession botSession) {

        var result = SendMessage.builder()
                .chatId(botSession.getId())
                .text("Неизвестная ошибка. Попробуйте снова.")
                .build();

        try {
            result = switch (botSession.getState()) {
                case MAIN -> mainMessage(botSession);
                case START -> startMessage(botSession);
                case ENTER_EMAIL -> emailMessage(botSession);
                case ENTER_PHONE_NUMBER -> phoneMessage(botSession);
                case CONFIRM -> confirmMessage(botSession);
            };
        } catch (EmptyBotUserException e) {
            log.error(e.toString());
        }

        return result;
    }

    private SendMessage mainMessage(BotSession session) {
        //TODO может быть засада с callbackQuery
        return BotUtil.createDefaultSendMessage(session.getId(), session.getMessage().getText().toUpperCase());
    }

    private SendMessage startMessage(BotSession session) {
        var text = "Чтобы использовать мой фунционал наполную, пройди регистрацию.";

        var response = BotUtil.createDefaultSendMessage(session.getId(), text);
        response.setReplyMarkup(Keyboards.registrationKeyboard());

        return response;
    }

    private SendMessage emailMessage(BotSession session) throws EmptyBotUserException {
        var text = "Введите свой номер телефона";
        var message = session.getMessage();
        var botUser = session.getBotUser();

        botUser.setEmail(message.getText());
        botStorage.saveBotUser(botUser);

        session.setBotUser(botUser);
        session.setState(ENTER_PHONE_NUMBER);
        botStorage.saveSession(session);

        return BotUtil.createDefaultSendMessage(session.getId(), text);
    }

    private SendMessage phoneMessage(BotSession session) throws EmptyBotUserException {
        var header = "Подтвердите ваши данные:\n\n";
        var footer = "\n\n*Нажмите одну из кнопок или введите Да/Нет";
        var message = session.getMessage();
        var botUser = session.getBotUser();

        botUser.setPhoneNumber(message.getText());
        botStorage.saveBotUser(botUser);

        session.setBotUser(botUser);
        session.setState(CONFIRM);
        botStorage.saveSession(session);

        var keyboard = Keyboards.confirmKeyboard();
        var response = BotUtil.createDefaultSendMessage(session.getId(), header + botUser + footer);
        response.setReplyMarkup(keyboard);
        return response;
    }

    private SendMessage confirmMessage(BotSession session) {
        var header = "Неизвестный текст или команда\n\nПодтвердите ваши данные:\n\n";

        var botUser = session.getBotUser();

        var keyboard = Keyboards.confirmKeyboard();
        var response = BotUtil.createDefaultSendMessage(session.getId(), header + botUser);
        response.setReplyMarkup(keyboard);

        return response;
    }
}