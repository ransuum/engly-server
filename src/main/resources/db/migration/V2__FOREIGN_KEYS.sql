CREATE INDEX IF NOT EXISTS idx_user_read_at ON message_reads (user_id, read_at);
CREATE INDEX IF NOT EXISTS idx_message_user ON message_reads (message_id, user_id);
CREATE INDEX IF NOT EXISTS idx_room_created ON messages (room_id, created_at);
CREATE INDEX IF NOT EXISTS idx_user_created ON messages (user_id, created_at);

-- Unique constraints with conditional checks
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uc_categories_name') THEN
            ALTER TABLE categories ADD CONSTRAINT uc_categories_name UNIQUE (name);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uc_moderation_user') THEN
            ALTER TABLE moderation ADD CONSTRAINT uc_moderation_user UNIQUE (user_id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uc_users_email') THEN
            ALTER TABLE users ADD CONSTRAINT uc_users_email UNIQUE (email_id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uc_users_user_name') THEN
            ALTER TABLE users ADD CONSTRAINT uc_users_user_name UNIQUE (user_name);
        END IF;

        -- Foreign key constraints
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_activity_logs_on_user') THEN
            ALTER TABLE activity_logs ADD CONSTRAINT FK_ACTIVITY_LOGS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_additional_info_on_id') THEN
            ALTER TABLE additional_info ADD CONSTRAINT FK_ADDITIONAL_INFO_ON_ID FOREIGN KEY (id) REFERENCES users (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_chat_participants_on_room') THEN
            ALTER TABLE chat_participants ADD CONSTRAINT FK_CHAT_PARTICIPANTS_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_chat_participants_on_user') THEN
            ALTER TABLE chat_participants ADD CONSTRAINT FK_CHAT_PARTICIPANTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_messages_on_room') THEN
            ALTER TABLE messages ADD CONSTRAINT FK_MESSAGES_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_messages_on_user') THEN
            ALTER TABLE messages ADD CONSTRAINT FK_MESSAGES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_message_reads_on_message') THEN
            ALTER TABLE message_reads ADD CONSTRAINT FK_MESSAGE_READS_ON_MESSAGE FOREIGN KEY (message_id) REFERENCES messages (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_message_reads_on_user') THEN
            ALTER TABLE message_reads ADD CONSTRAINT FK_MESSAGE_READS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_moderation_on_moder') THEN
            ALTER TABLE moderation ADD CONSTRAINT FK_MODERATION_ON_MODER FOREIGN KEY (moder_id) REFERENCES users (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_moderation_on_room') THEN
            ALTER TABLE moderation ADD CONSTRAINT FK_MODERATION_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_moderation_on_user') THEN
            ALTER TABLE moderation ADD CONSTRAINT FK_MODERATION_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_notifications_on_user') THEN
            ALTER TABLE notifications ADD CONSTRAINT FK_NOTIFICATIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_refresh_tokens_on_user') THEN
            ALTER TABLE refresh_tokens ADD CONSTRAINT FK_REFRESH_TOKENS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_rooms_on_category') THEN
            ALTER TABLE rooms ADD CONSTRAINT FK_ROOMS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_rooms_on_creator') THEN
            ALTER TABLE rooms ADD CONSTRAINT FK_ROOMS_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_settings_on_id') THEN
            ALTER TABLE user_settings ADD CONSTRAINT FK_USER_SETTINGS_ON_ID FOREIGN KEY (id) REFERENCES users (id);
        END IF;
    END$$;
