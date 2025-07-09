CREATE TABLE categories
(
    id          VARCHAR(255)                NOT NULL,
    name        VARCHAR(255)                NOT NULL,
    description VARCHAR(255)                NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE rooms
(
    id          VARCHAR(255)                NOT NULL,
    category_id VARCHAR(255),
    name        VARCHAR(255)                NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    creator_id  VARCHAR(255),
    CONSTRAINT pk_rooms PRIMARY KEY (id)
);

CREATE TABLE chat_participants
(
    id        VARCHAR(255)                NOT NULL,
    room_id   VARCHAR(255),
    user_id   VARCHAR(255),
    joined_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    leave_at  TIMESTAMP WITHOUT TIME ZONE,
    role      VARCHAR(255),
    CONSTRAINT pk_chat_participants PRIMARY KEY (id)
);