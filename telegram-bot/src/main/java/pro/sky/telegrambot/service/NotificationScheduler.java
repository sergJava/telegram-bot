package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationScheduler {
    private final TelegramBot telegramBot;
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationScheduler(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
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
