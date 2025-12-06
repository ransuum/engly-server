package com.engly.engly_server.utils;

public class CacheName {

    private CacheName() {
        // Private constructor to prevent instantiation
    }

    public static final String USER_ID = "user-by-id";
    public static final String ALL_USER = "all-users";
    public static final String USER_BY_EMAIL = "user-by-email";
    public static final String USER_PROFILES = "user-profiles";
    public static final String USER_EXISTS_BY_ID = "user-exists-by-id";
    public static final String USER_ID_BY_EMAIL = "user-id-by-email";
    public static final String USER_ENTITY_ID = "user_entity_by_id";
    public static final String USER_SETTINGS = "userSettings";

    public static final String ROOM_DTO_ID = "room-dto-by-id";
    public static final String ROOMS_BY_CATEGORY = "rooms-by-category";
    public static final String ROOMS_BY_CRITERIA = "rooms-by-criteria";
    public static final String ROOM_ENTITY_ID = "room-entity-by-id";
    public static final String ROOM_SHORT_ID = "room-short-by-id";

    public static final String CATEGORY_ENTITY_ID = "category-entity-by-id";
    public static final String CATEGORY_NAME = "category-by-name";
    public static final String CATEGORY_ID_BY_NAME = "category-id-by-name";
    public static final String ALL_CATEGORIES = "all-categories";

    public static final String MESSAGE_ID = "message-by-id";
    public static final String MESSAGES_BY_ROOM_NATIVE = "messages-by-room-native";
    public static final String MESSAGES_BY_CRITERIA = "messages-by-criteria";
    public static final String MESSAGE_COUNT_BY_ROOM = "message-count-by-room";

    public static final String PARTICIPANTS_BY_ROOM = "participants-by-room";
    public static final String PARTICIPANT_EXISTS = "participant-exists";
    public static final String COUNT_PARTICIPANTS = "participant-count";

    public static final String MESSAGE_READ_STATUS = "message-read-status";
    public static final String USERS_WHO_READ_MESSAGE = "users-who-read-message";
    public static final String USERNAME_BY_EMAIL = "username-by-email";
}
