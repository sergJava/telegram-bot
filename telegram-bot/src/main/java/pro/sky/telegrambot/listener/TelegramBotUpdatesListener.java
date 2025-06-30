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

    String messageText;
    Long chatId;
    Pattern pattern = Pattern.compile("[(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)]");


    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String text = update.message().text();
            if (text.equals("/start")) {
                chatId = update.message().chat().id();
                messageText = "Our bot welcomes you";
                SendMessage message = new SendMessage(chatId, messageText);
                telegramBot.execute(message);
            }
            Matcher matcher = pattern.matcher(text);
            if(matcher.matches()){
                String timeString = matcher.group(1);
                messageText = matcher.group(2);
                chatId = update.message().chat().id();
                LocalDateTime notificationTime = LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                NotificationTask notificationTask = new NotificationTask(notificationTime, messageText, chatId);
                notificationTaskRepository.save(notificationTask);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void run(){
        List<NotificationTask> currentTasks = choiceNotifications();
        for (NotificationTask currentTask : currentTasks) {
            SendMessage sendMessage = new SendMessage(currentTask.getChatId(), currentTask.getMessageText());
            telegramBot.execute(sendMessage);
        }
    }

    public List<NotificationTask> choiceNotifications(){
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        return notificationTaskRepository.findCurrentTasks(currentTime);
    }

}
