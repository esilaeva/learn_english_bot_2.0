package com.ilana.bot.service;

import com.ilana.bot.config.BotConfig;
import com.ilana.bot.model.EnglishWord;
import com.ilana.bot.model.RussianWord;
import com.ilana.bot.model.User;
import com.ilana.bot.model.UserProgress;
import com.ilana.bot.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.ilana.bot.service.Constants.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    Keyboards keyboard = new Keyboards();
    String language, word, choice;
    Long wordId, count;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private UserProgressRepository userProgressRepository;
    @Autowired
    private RussianWordRepository russianWordRepository;
    @Autowired
    private EnglishWordRepository englishWordRepository;
    @Autowired
    private WordTranslationRepository wordTranslationRepository;

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "начинаем"));
        listOfCommands.add(new BotCommand("/dict", "словарь"));
        listOfCommands.add(new BotCommand("/learn", "учим слова"));
        listOfCommands.add(new BotCommand("/user info", "информация о пользователе"));
        listOfCommands.add(new BotCommand("/help", "справка"));
        try {
            execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start", "/back" -> {
                    start(chatId, update.getMessage().getChat().getFirstName(), update.getMessage());
                }
                case "/dict" -> {
                    userRepository.updateChoiceByChatId(DICT, chatId);
                    dict(chatId);
                }
                case "/help" -> prepareAndSendMessage(chatId, HELP_TEXT);
                case "/learn" -> {
                    userRepository.updateChoiceByChatId(LEARN, chatId);
                    learn(chatId);
                }
                default -> {
                    choice = userRepository.findUserDataByChatId(chatId).getChoice();
                    if (choice.equals(DICT)) {
                        dictFind(messageText, chatId);
                    } else {
                        findTranslateWord(messageText, chatId);
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case LEARN -> {
                    userRepository.updateChoiceByChatId(callbackData, chatId);
                    learn(chatId);
                }
                case DICT -> {
                    userRepository.updateChoiceByChatId(callbackData, chatId);
                    dict(chatId);
                }
                case RU, EN -> {
                    userRepository.updateLanguageByChatId(callbackData, chatId);
                    findRandomWord(chatId);
                }
                case NEXT -> {
                    choice = userRepository.findUserDataByChatId(chatId).getChoice();
                    if (choice.equals(LEARN)) {
                        findRandomWord(chatId);
                    } else if (choice.equals(DICT)) {
                        dict(chatId);
                    } else if (choice.isEmpty()) {
                        start(chatId, update.getMessage().getChat().getFirstName(), update.getMessage());
                    }
                }
                case TRANSLATE -> {
                    if (userRepository.findUserDataByChatId(chatId).getLanguage().equals(RU)) {
                        word = String.valueOf(userRepository.findUserDataByChatId(chatId).getLastRuWordId());
                        findAllTranslates(word, chatId);
                    } else if (userRepository.findUserDataByChatId(chatId).getLanguage().equals(EN)) {
                        word = String.valueOf(userRepository.findUserDataByChatId(chatId).getLastEnWordId());
                        findAllTranslates(word, chatId);
                    }
                }
                case BACK -> back(chatId);
                case COUNT -> resetCounter(chatId);
            }
        }
    }

    private void resetCounter(long chatId) {
        language = userRepository.findUserDataByChatId(chatId).getLanguage();
        if (language.equals(RU)) {
//            wordsRepository.updateCountWordRuByChatId();
            userProgressRepository.updateWordCounterRuByLanguageAndChatId(language, chatId);
            back(chatId);
        } else {
            userProgressRepository.updateWordCounterEnByLanguageAndChatId(language, chatId);
//            wordsRepository.updateCountWordEnByChatId();
            back(chatId);
        }
    }

    private void dict(long chatId) {
        executeMessageText(FIND_WORD, chatId);
    }

    private void dictFind(String str, long chatId) {
        identifyLanguage(str, chatId);
        findAllTranslates(str, chatId);
    }

    private void identifyLanguage(String message, long chatId) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(message.charAt(0));
        if (Character.UnicodeBlock.CYRILLIC.equals(block)) {
            language = RU;

        } else if (Character.UnicodeBlock.BASIC_LATIN.equals(block)) {
            language = EN;
        }
        userRepository.updateLanguageByChatId(language, chatId);
    }

    private void findAllTranslates(String word, long chatId) {
        String answer = "";
        if (word == null) {
            answer = WORD_IS_NOT_EXISTS;
        }
        executeMessageTextAddKeyboard(answer, chatId, "navShort");
    }

    private void findRandomWord(long chatId) {
        String textSend, trans = "";
        List<UserProgress> userProgressData = userProgressRepository.findByChatId(chatId);
        User userData = userRepository.findUserDataByChatId(chatId);
        language = userData.getLanguage();
        boolean flag = false;
        if (language.equals(RU)) {
            RussianWord russianWord = russianWordRepository.findRandomWord();
            wordId = russianWord.getId();
            word = russianWord.getWord();
            if (!userProgressData.isEmpty()) {
                for (UserProgress u : userProgressData) {
                    if (u.getWordId().equals(wordId)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                count = 0L;
                addUserProgress(chatId, language, wordId, count);
            }
            count = userProgressRepository.findCounterByLanguageAndWordId(language, wordId);
            if(count == null){
                count = 0L;
            }
            if (count >= 5) {
                executeMessageTextAddKeyboard(ALL_DONE, chatId, "count");
                return;
            }
        } else if (language.equals(EN)) {
            EnglishWord englishWord = englishWordRepository.findRandomWord();
            wordId = englishWord.getId();
            word = englishWord.getWord();
            trans = englishWord.getTranscription().equals("[]") ? "" : " " + englishWord.getTranscription();
            if (!userProgressData.isEmpty()) {
                for (UserProgress u : userProgressData) {
                    if (u.getWordId().equals(wordId)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                count = 0L;
                addUserProgress(chatId, language, wordId, count);
            }
            count = userProgressRepository.findCounterByLanguageAndWordId(language, wordId);
            if (count >= 5) {
                executeMessageTextAddKeyboard(ALL_DONE, chatId, "count");
                return;
            }
        }
        textSend = String.format("Твой вариант перевода слова <b> %s%s </b>:", word, trans);
        executeMessageText(textSend, chatId);
    }

    private void addUserProgress(Long chatId, String language, Long wordId, Long count) {
            UserProgress userProgress = new UserProgress();
            userProgress.setChatId(chatId);
            userProgress.setLanguage(language);
            userProgress.setWordId(wordId);
        userProgress.setWordCounter(count);

            userProgressRepository.save(userProgress);
    }

    private void findTranslateWord(String message, long chatId) {
        User userData = userRepository.findUserDataByChatId(chatId);
        String textSend = "";
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        boolean flag = false;

        if (language.equals(EN)) {
            var userProgress = userProgressRepository.findByChatIdAndWordIdAndLanguage(chatId, wordId, language);
            if (userProgress == null) {
                addUserProgress(chatId, language, wordId, count);
            } else {
                count = userProgress.getWordCounter();
            }
            List<EnglishWord> list = wordTranslationRepository.findEnglishTranslationsForRussianWord(message.toLowerCase());
            for (EnglishWord ew : list) {
                if (ew.getWord().equals(word)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                if (count != null && count < 5) {
                    count += 1;
                    userProgressRepository.updateCountWordByLanguageAndWordId(count, language, wordId);
                    textSend = ANSWER_RIGHT;
                } else {
                    textSend = ALL_DONE;
                }
            } else {
                textSend = ANSWER_WRONG;
            }
        } else if (language.equals(RU)) {
            var userProgress = userProgressRepository.findByChatIdAndWordIdAndLanguage(chatId, wordId, language);

            if (userProgress == null) {
                addUserProgress(chatId, language, wordId, count);
            } else {
                count = userProgress.getWordCounter();
            }
            List<RussianWord> list = wordTranslationRepository.findRussianTranslationsForEnglishWord(message.toLowerCase());
            for (RussianWord rw : list) {
                if (rw.getWord().equals(word)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                if (count != null && count < 5) {
                    count += 1;
                    userProgressRepository.updateCountWordByLanguageAndWordId(count, language, wordId);
                    textSend = ANSWER_RIGHT;
                } else {
                    textSend = ALL_DONE;
                }
            } else {
                textSend = ANSWER_WRONG;
            }
        }
        executeMessageTextAddKeyboard(textSend, chatId, "nav");
    }

    private void learn(long chatId) {
        executeMessageTextAddKeyboard(CHOOSE_LANGUAGE, chatId, "lang");
    }

    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("registerUser/User saved: " + user);
        }
    }

    private void start(long chatId, String name, Message message) {
        String answer = "Привет, " + name + ", что будем делать? Выбирай:";
        registerUser(message);

        executeMessageTextAddKeyboard(answer, chatId, "menu");
    }

    private void back(long chatId) {
        executeMessageTextAddKeyboard(NEW_CHOOSE, chatId, "menu");
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setParseMode("HTML");

        executeMessage(message);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);
        message.setParseMode("HTML");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessageText(String text, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setParseMode("HTML");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessageTextAddKeyboard(String text, long chatId, String kb) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setParseMode("HTML");

        switch (kb) {
            case "menu" -> keyboard.menuKeyboard(message);
            case "nav" -> keyboard.navigationKeyboard(message);
            case "navShort" -> keyboard.navigationShortKeyboard(message);
            case "lang" -> keyboard.languageKeyboard(message);
            case "count" -> keyboard.counterKeyboard(message);
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            message.setParseMode("HTML");
            execute(message);
        } catch (TelegramApiException e) {
            log.error("error occurred: " + e.getMessage());
        }
    }

    private void prepareAndSendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        executeMessage(message);
    }
}
