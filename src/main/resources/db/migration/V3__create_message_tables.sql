CREATE TABLE messages
(
    id         VARCHAR(255)                NOT NULL,
    room_id    VARCHAR(255),
    user_id    VARCHAR(255)                NOT NULL,
    content    VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    is_edited  BOOLEAN DEFAULT FALSE       NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE       NOT NULL,
    CONSTRAINT pk_messages PRIMARY KEY (id)
);

CREATE TABLE message_reads
(
    message_id VARCHAR(255)                NOT NULL,
    user_id    VARCHAR(255)                NOT NULL,
    read_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_message_reads PRIMARY KEY (message_id, user_id)
);