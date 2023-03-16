package ru.malakhov.telegrambot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.malakhov.telegrambot.bot.BotSession;
import ru.malakhov.telegrambot.exception.EmptyBotUserException;

@Slf4j
@Component
public class CallbackHandler extends AbstractHandler {
    public CallbackHandler(TextHandler textHandler,
                           CommandHandler commandHandler) {
        super(textHandler, commandHandler);
    }

    @Override
    public void handling(BotSession botSession) {
        SendMessage response;
        var callbackData = botSession.getCallbackData();

        if (callbackData.startsWith("/")) {
            botSession.setBotCommand(callbackData);
            response = commandHandler.handling(botSession);
        } else {
            response = textHandler.handling(botSession);
        }
        bot.sendingMessage(response);
    }
}