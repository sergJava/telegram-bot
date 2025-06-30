package pro.sky.telegrambot.sender;

import org.springframework.scheduling.annotation.Scheduled;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Sender {
    private final NotificationTaskRepository notificationTaskRepository;

//    @Scheduled(cron = "0 0/1 * * * *")
//    public void run()

    public Sender(NotificationTaskRepository notificationTaskRepository){
        this.notificationTaskRepository = notificationTaskRepository;
    }

    public List<NotificationTask> choiceNotifications(){
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        return notificationTaskRepository.findCurrentTasks(currentTime);
    }
}
