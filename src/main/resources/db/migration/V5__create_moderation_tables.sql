CREATE TABLE moderation
(
    id         VARCHAR(255)                NOT NULL,
    room_id    VARCHAR(255),
    moder_id   VARCHAR(255),
    user_id    VARCHAR(255),
    action     VARCHAR(255)                NOT NULL,
    reason     VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_moderation PRIMARY KEY (id)
);