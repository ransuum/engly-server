package com.engly.engly_server.utils.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheName {
    public static final String USER_ID = "user-by-id";
    public static final String ALL_USER = "all-users";
    public static final String USER_BY_EMAIL = "user-by-email";
    public static final String USER_BY_EMAIL_DTO = "user-by-email-dto";
    public static final String USER_PROFILES = "user-profiles";
    public static final String USER_ID_BY_EMAIL = "user-id-by-email";
    public static final String USER_ENTITY_BY_EMAIL = "user-entity-by-email";
    public static final String USER_SETTINGS = "userSettings";

    public static final String ROOM_DTO_ID = "room-dto-by-id";
    public static final String ROOMS_BY_CATEGORY = "rooms-by-category";
    public static final String ROOMS_BY_CRITERIA = "rooms-by-criteria";
    public static final String ROOM_ENTITY_ID = "room-entity-by-id";

    public static final String CATEGORY_ENTITY_ID = "category-entity-by-id";
    public static final String CATEGORY_NAME = "category-by-name";
    public static final String ALL_CATEGORIES = "all-categories";

    public static final String MESSAGE_ID = "message-by-id";
    public static final String MESSAGES_BY_ROOM_NATIVE = "messages-by-room-native";
    public static final String MESSAGES_BY_CRITERIA = "messages-by-criteria";
    public static final String MESSAGE_COUNT_BY_ROOM = "message-count-by-room";

    public static final String PARTICIPANTS_BY_ROOM = "participants-by-room";
    public static final String PARTICIPANT_EXISTS = "participant-exists";

    public static final String MESSAGE_READ_STATUS = "message-read-status";
    public static final String USERS_WHO_READ_MESSAGE = "users-who-read-message";
    public static final String USERNAME_BY_EMAIL = "username-by-email";

    public static final List<String> CACHES = Arrays.asList(
            USER_ID, ALL_USER, USER_BY_EMAIL, USER_PROFILES, USER_ID_BY_EMAIL, USERNAME_BY_EMAIL,ROOM_DTO_ID,
            USER_SETTINGS, USER_ENTITY_BY_EMAIL, ROOMS_BY_CATEGORY, ROOM_ENTITY_ID, ROOMS_BY_CRITERIA,
            CATEGORY_ENTITY_ID, CATEGORY_NAME, ALL_CATEGORIES, MESSAGE_ID, USER_BY_EMAIL_DTO,
            MESSAGES_BY_ROOM_NATIVE, MESSAGE_COUNT_BY_ROOM, MESSAGES_BY_CRITERIA, PARTICIPANTS_BY_ROOM,
            PARTICIPANT_EXISTS, MESSAGE_READ_STATUS, USERS_WHO_READ_MESSAGE
    );
}
