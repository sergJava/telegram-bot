package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_task")
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String messageText;
    private LocalDateTime notification_time;

    public NotificationTask(){}

    public NotificationTask(LocalDateTime notification_time, String messageText, Long chatId) {
        this.notification_time = notification_time;
        this.messageText = messageText;
        this.chatId = chatId;
    }

    public Long getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getMessageText(){
        return messageText;
    }

    public LocalDateTime getNotification_time() {
        return notification_time;
    }


}
