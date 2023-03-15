package ru.malakhov.telegrambot.kyeboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Keyboards {

    public static InlineKeyboardMarkup confirmKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        var rows = new ArrayList<List<InlineKeyboardButton>>();

        var rowConfirm = new ArrayList<InlineKeyboardButton>();
        var buttonConfirm = new InlineKeyboardButton("Подтвердить");
        buttonConfirm.setCallbackData("/confirm");
        rowConfirm.add(buttonConfirm);

        var rowChange = new ArrayList<InlineKeyboardButton>();
        var buttonChange = new InlineKeyboardButton("Изменить");
        buttonChange.setCallbackData("/change");
        rowChange.add(buttonChange);

        var rowCancel = new ArrayList<InlineKeyboardButton>();
        var buttonCancel = new InlineKeyboardButton("Отменить");
        buttonCancel.setCallbackData("/cancel");
        rowCancel.add(buttonCancel);

        rows.add(rowConfirm);
        rows.add(rowChange);
        rows.add(rowCancel);
        keyboard.setKeyboard(rows);

        return keyboard;
    }
}
