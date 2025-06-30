package com.engly.engly_server.utils.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheName {
    public static final String USER_ID = "users";
    public static final String ALL_USER = "allUsers";
    public static final String USER_BY_EMAIL = "userByEmail";
    public static final String USER_PROFILES = "userProfiles";

    public static final String ROOM_ID = "roomById";
    public static final String ROOMS_BY_CATEGORY = "roomsByCategory";
    public static final String ROOM_SEARCH_RESULTS = "roomSearchResults";
    public static final String ROOM_ENTITY_ID = "roomEntityById";
    public static final String ROOMS = "rooms";

    public static final String CATEGORY_ID = "categoryById";
    public static final String CATEGORY_ENTITY_ID = "categoryEntityById";
    public static final String CATEGORY_NAME = "categoryByName";
    public static final String ALL_CATEGORIES = "allCategories";

    public static final String MESSAGE_ID = "messageById";
    public static final String MESSAGES_BY_ROOM = "messagesByRoom";
    public static final String MESSAGES_SEARCH_RESULTS = "messageSearchResults";

    public static final String PARTICIPANTS_BY_ROOM = "participantsByRoom";
    public static final String PARTICIPANT_EXISTS = "participant-exists";

    public static final String MESSAGE_READ_STATUS = "messageReadStatus";
    public static final String USERS_WHO_READ_MESSAGE = "usersWhoReadMessage";

    public static final List<String> CACHES = Arrays.asList(
            USER_ID,
            ALL_USER,
            USER_PROFILES,
            USER_BY_EMAIL,

            ROOM_ID,
            ROOMS_BY_CATEGORY,
            ROOM_ENTITY_ID,
            ROOM_SEARCH_RESULTS,
            ROOMS,

            CATEGORY_ID,
            CATEGORY_NAME,
            ALL_CATEGORIES,
            CATEGORY_ENTITY_ID,

            MESSAGE_ID,
            MESSAGES_BY_ROOM,
            MESSAGES_SEARCH_RESULTS,

            PARTICIPANTS_BY_ROOM,
            PARTICIPANT_EXISTS,

            MESSAGE_READ_STATUS,
            USERS_WHO_READ_MESSAGE);
}
