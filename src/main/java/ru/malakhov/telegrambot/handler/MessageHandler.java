package ru.malakhov.telegrambot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.malakhov.telegrambot.bot.BotSession;

@Slf4j
@Component
public class MessageHandler extends AbstractHandler {
    public MessageHandler(CommandHandler commandHandler,
                          TextHandler textHandler) {
        super(textHandler, commandHandler);
    }

    @Override
    public void handling(BotSession botSession) {
        SendMessage response;
        var inputText = botSession.getMessage().getText().split(" ")[0];

        if (inputText.startsWith("/")) {
            botSession.setBotCommand(inputText);
            response = commandHandler.handling(botSession);
        } else {
            response = textHandler.handling(botSession);
        }
        bot.sendingMessage(response);
    }
}