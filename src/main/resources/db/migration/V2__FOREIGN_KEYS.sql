CREATE INDEX idx_user_read_at ON message_reads (user_id, read_at);
CREATE INDEX idx_message_user ON message_reads (message_id, user_id);
CREATE INDEX idx_room_created ON messages (room_id, created_at);
CREATE INDEX idx_user_created ON messages (user_id, created_at);

-- Unique constraints
ALTER TABLE categories
    ADD CONSTRAINT uc_categories_name UNIQUE (name);

ALTER TABLE moderation
    ADD CONSTRAINT uc_moderation_user UNIQUE (user_id);

ALTER TABLE rooms
    ADD CONSTRAINT uc_rooms_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_user_name UNIQUE (user_name);

-- Foreign key constraints
ALTER TABLE activity_logs
    ADD CONSTRAINT FK_ACTIVITY_LOGS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE additional_info
    ADD CONSTRAINT FK_ADDITIONAL_INFO_ON_ID FOREIGN KEY (id) REFERENCES users (id);

ALTER TABLE chat_participants
    ADD CONSTRAINT FK_CHAT_PARTICIPANTS_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);

ALTER TABLE chat_participants
    ADD CONSTRAINT FK_CHAT_PARTICIPANTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE message_reads
    ADD CONSTRAINT FK_MESSAGE_READS_ON_MESSAGE FOREIGN KEY (message_id) REFERENCES messages (id);

ALTER TABLE message_reads
    ADD CONSTRAINT FK_MESSAGE_READS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE moderation
    ADD CONSTRAINT FK_MODERATION_ON_MODER FOREIGN KEY (moder_id) REFERENCES users (id);

ALTER TABLE moderation
    ADD CONSTRAINT FK_MODERATION_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);

ALTER TABLE moderation
    ADD CONSTRAINT FK_MODERATION_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT FK_REFRESH_TOKENS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE rooms
    ADD CONSTRAINT FK_ROOMS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE rooms
    ADD CONSTRAINT FK_ROOMS_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (id);

ALTER TABLE user_settings
    ADD CONSTRAINT FK_USER_SETTINGS_ON_ID FOREIGN KEY (id) REFERENCES users (id);