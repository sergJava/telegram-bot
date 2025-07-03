package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final NotificationTaskRepository notificationTaskRepository;

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository){
        this.notificationTaskRepository = notificationTaskRepository;
    }

    private static final Pattern pattern = Pattern.compile("^(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})\\s+(.+)$");


    @Override
    public int process(List<Update> updates) {
        String messageText;
        Long chatId;

        for (Update update : updates) {
            if (update.message() != null && update.message().text() != null) {
                String text = update.message().text();
                chatId = update.message().chat().id();

                if (text.equals("/start")) {
                    messageText = "Привет! Я бот-напоминалка. Напиши мне сообщение в формате:\n" +
                            "01.01.2025 18:30 Сделать домашку";
                    telegramBot.execute(new SendMessage(chatId, messageText));
                } else {
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.matches()) {
                        try {
                            String timeString = matcher.group(1);
                            messageText = matcher.group(2);
                            System.out.println("Parsed: " + timeString + messageText);
                            LocalDateTime notificationTime = LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                            NotificationTask task = new NotificationTask(notificationTime, messageText, chatId);
                            notificationTaskRepository.save(task);
                            telegramBot.execute(new SendMessage(chatId, "Задача сохранена! Я напомню в указанное время."));
                        } catch (DateTimeParseException e) {
                            logger.warn("ошибка при парсинге даты у пользователя {}, {}", chatId, e.getMessage());
                            telegramBot.execute(new SendMessage(chatId, "Ошибка при обработке даты. Убедись, что формат правильный: dd.MM.yyyy HH:mm"));
                        }
                    } else {
                        telegramBot.execute(new SendMessage(chatId, "Неверный формат! Пример:\n01.01.2025 18:30 Сделать домашку"));
                    }
                }
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }




}
