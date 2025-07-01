package pro.sky.telegrambot.repository;

import org.hibernate.query.NativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    @Query(value = "SELECT * FROM notification_task WHERE notification_time = :someTime", nativeQuery = true)
    List<NotificationTask> findCurrentTasks(@Param("someTime") LocalDateTime someTime);
}
