ALTER TABLE chat_room_tb MODIFY id BINARY(16) NOT NULL;
ALTER TABLE chat_room_tb DROP COLUMN sender_id;
ALTER TABLE chat_room_tb DROP COLUMN creator_id;
ALTER TABLE chat_room_tb ADD COLUMN cursor_id BIGINT NOT NULL;
ALTER TABLE chat_room_tb ADD COLUMN created_at TIMESTAMP NOT NULL;
ALTER TABLE chat_room_tb ADD UNIQUE KEY `UK_project_id` (`project_id`);

CREATE TABLE IF NOT EXISTS chat_user_mapping_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL ,
    chat_room_id BINARY(16) NOT NULL,
    user_id BIGINT NOT NULL,
    available_yn TINYINT NOT NULL,
    UNIQUE KEY `UK_chat_room_id_user_id` (`chat_room_id`, `user_id`)
)