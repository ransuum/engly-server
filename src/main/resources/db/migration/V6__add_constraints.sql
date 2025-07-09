ALTER TABLE categories ADD CONSTRAINT uc_categories_name UNIQUE (name);
ALTER TABLE moderation ADD CONSTRAINT uc_moderation_user UNIQUE (user_id);
ALTER TABLE user_settings ADD CONSTRAINT uc_user_settings_user UNIQUE (user_id);
ALTER TABLE users ADD CONSTRAINT uc_users_additional_info UNIQUE (additional_info_id);
ALTER TABLE users ADD CONSTRAINT uc_users_email UNIQUE (email_id);
ALTER TABLE users ADD CONSTRAINT uc_users_user_name UNIQUE (user_name);