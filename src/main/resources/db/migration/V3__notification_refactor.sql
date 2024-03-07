ALTER TABLE mogakgo.chat_room_tb ADD UNIQUE KEY (cursor_id);
ALTER TABLE mogakgo.chat_room_tb MODIFY COLUMN cursor_id BIGINT AUTO_INCREMENT;
ALTER TABLE mogakgo.notification_tb DROP COLUMN sender_id;
ALTER TABLE mogakgo.notification_tb DROP COLUMN detail_data;
ALTER TABLE mogakgo.notification_tb ADD COLUMN user_id BIGINT NOT NULL;
ALTER TABLE mogakgo.notification_tb ADD COLUMN project_id BIGINT;
ALTER TABLE mogakgo.notification_tb ADD COLUMN achievement_id BIGINT;
ALTER TABLE mogakgo.notification_tb ADD COLUMN message VARCHAR(255);