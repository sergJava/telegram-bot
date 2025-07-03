-- liquibase formatted sql
-- changeset sfibikh:1
CREATE TABLE notification_task(
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  chat_id BIGINT NOT NULL,
  message_text TEXT NOT NULL,
  notification_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);