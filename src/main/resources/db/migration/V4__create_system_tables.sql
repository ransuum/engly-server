CREATE TABLE notifications
(
    id         VARCHAR(255)                NOT NULL,
    user_id    VARCHAR(255),
    content    VARCHAR(255)                NOT NULL,
    is_read    BOOLEAN,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE TABLE user_settings
(
    id                 VARCHAR(255) NOT NULL,
    user_id            VARCHAR(255),
    theme              VARCHAR(255),
    notifications      BOOLEAN      NOT NULL,
    interface_language VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user_settings PRIMARY KEY (id)
);

CREATE TABLE activity_logs
(
    id         VARCHAR(255)                NOT NULL,
    user_id    VARCHAR(255),
    action     VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_activity_logs PRIMARY KEY (id)
);

CREATE TABLE statistics
(
    id                VARCHAR(255)                NOT NULL,
    room_id           VARCHAR(255),
    message_count     BIGINT                      NOT NULL,
    last_message_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_statistics PRIMARY KEY (id)
);