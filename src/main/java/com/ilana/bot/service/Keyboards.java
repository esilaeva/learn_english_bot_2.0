package com.ilana.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.ilana.bot.service.Constants.*;

@Slf4j
public class Keyboards {

    public void menuKeyboard(SendMessage message) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Учить слова");
        yesButton.setCallbackData(LEARN);

        var noButton = new InlineKeyboardButton();

        noButton.setText("Словарь");
        noButton.setCallbackData(DICT);

        rowInline.add(yesButton);
        rowInline.add(noButton);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        message.setReplyMarkup(markupInline);
    }

    public void topicKeyboard(SendMessage message, List<String> listTopic) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton allWordsButton = new InlineKeyboardButton();
        allWordsButton.setText("Все слова из базы");
        allWordsButton.setCallbackData(ALL_WORDS);

        for (int i = 0; i < listTopic.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(listTopic.get(i));
            button.setCallbackData("Button_" + listTopic.get(i));
            rowInline.add(button);

            if ((i % 2 != 0) || (i == listTopic.size() - 1)) {
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
            }
        }

        List<InlineKeyboardButton> allWordsRow = new ArrayList<>();
        allWordsRow.add(allWordsButton);
        rowsInline.add(allWordsRow);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
    }

    public void languageKeyboard(SendMessage message) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        var button1 = new InlineKeyboardButton();
        button1.setText("English");
        button1.setCallbackData(EN);

        var button2 = new InlineKeyboardButton();
        button2.setText("Русский");
        button2.setCallbackData(RU);

        rowInline.add(button1);
        rowInline.add(button2);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        message.setReplyMarkup(markupInline);
    }

    public void navigationKeyboard(SendMessage message) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();

        var button1 = new InlineKeyboardButton();
        button1.setText("Перевод");
        button1.setCallbackData(TRANSLATE);

        var button2 = new InlineKeyboardButton();
        button2.setText("Next");
        button2.setCallbackData(NEXT);

        var button3 = new InlineKeyboardButton();
        button3.setText("Вернуться в начало");
        button3.setCallbackData(BACK);

        rowInline.add(button1);
        rowInline.add(button2);
        rowsInline.add(rowInline);

        rowInline2.add(button3);
        rowsInline.add(rowInline2);

        markupInline.setKeyboard(rowsInline);

        message.setReplyMarkup(markupInline);
    }

    public void navigationShortKeyboard(SendMessage message) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        var button1 = new InlineKeyboardButton();
        button1.setText("Next");
        button1.setCallbackData(NEXT);

        var button2 = new InlineKeyboardButton();
        button2.setText("Вернуться в начало");
        button2.setCallbackData(BACK);

        rowInline.add(button1);
        rowInline.add(button2);
        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);

        message.setReplyMarkup(markupInline);
    }

    public void counterKeyboard(SendMessage message) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        var button1 = new InlineKeyboardButton();
        button1.setText("Сбросить счетчики");
        button1.setCallbackData(COUNT);

        var button2 = new InlineKeyboardButton();
        button2.setText("Вернуться в начало");
        button2.setCallbackData(BACK);

        rowInline.add(button1);
        rowInline.add(button2);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        message.setReplyMarkup(markupInline);
    }
}
