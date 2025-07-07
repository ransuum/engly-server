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
    public static final String USER_PROFILES = "user-profiles";
    public static final String USERNAME_AVAILABILITY = "username-availability";
    public static final String EMAIL_AVAILABILITY = "email-availability";
    public static final String USER_FIRST_LOGIN = "user-first-login";

    public static final String ROOM_ID = "room-by-id";
    public static final String ROOM_DTO_ID = "room-dto-by-id";
    public static final String ROOMS_BY_CATEGORY = "rooms-by-category";
    public static final String ROOM_SEARCH_RESULTS = "room-search-results";
    public static final String ROOM_ENTITY_ID = "room-entity-by-id";
    public static final String ROOM_STATS = "room-stats";

    public static final String CATEGORY_ID = "category-by-id";
    public static final String CATEGORY_ENTITY_ID = "category-entity-by-id";
    public static final String CATEGORY_NAME = "category-by-name";
    public static final String ALL_CATEGORIES = "all-categories";
    public static final String ALL_CATEGORIES_LIST = "all-categories-list";

    public static final String MESSAGE_ID = "message-by-id";
    public static final String MESSAGES_BY_ROOM = "messages-by-room";           // JPA queries
    public static final String MESSAGES_BY_ROOM_NATIVE = "messages-by-room-native"; // Native queries
    public static final String MESSAGES_BY_ROOM_CURSOR = "messages-by-room-cursor"; // Cursor pagination
    public static final String MESSAGES_SEARCH_RESULTS = "message-search-results";
    public static final String MESSAGE_COUNT_BY_ROOM = "message-count-by-room";

    public static final String PARTICIPANTS_BY_ROOM = "participants-by-room";
    public static final String PARTICIPANT_EXISTS = "participant-exists";
    public static final String PARTICIPANT_COUNT = "participant-count";
    public static final String USER_ROOMS = "user-rooms";

    public static final String MESSAGE_READ_STATUS = "message-read-status";
    public static final String USERS_WHO_READ_MESSAGE = "users-who-read-message";
    public static final String MESSAGE_READ_COUNT = "message-read-count";
    public static final String USER_READ_MESSAGES = "user-read-messages";

    public static final List<String> CACHES = Arrays.asList(
            USER_ID, ALL_USER, USER_BY_EMAIL, USER_PROFILES, USERNAME_AVAILABILITY, EMAIL_AVAILABILITY, USER_FIRST_LOGIN,
            ROOM_ID, ROOM_DTO_ID, ROOMS_BY_CATEGORY, ROOM_SEARCH_RESULTS, ROOM_ENTITY_ID, ROOM_STATS,
            CATEGORY_ID, CATEGORY_ENTITY_ID, CATEGORY_NAME, ALL_CATEGORIES, ALL_CATEGORIES_LIST,
            MESSAGE_ID, MESSAGES_BY_ROOM, MESSAGES_SEARCH_RESULTS, MESSAGES_BY_ROOM_NATIVE, MESSAGE_COUNT_BY_ROOM,
            MESSAGES_BY_ROOM_CURSOR, PARTICIPANTS_BY_ROOM, PARTICIPANT_EXISTS, PARTICIPANT_COUNT, USER_ROOMS,
            MESSAGE_READ_STATUS, USERS_WHO_READ_MESSAGE, MESSAGE_READ_COUNT, USER_READ_MESSAGES
    );
}
