package com.engly.engly_server.utils.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheName {
    public static final String USER_ID = "users";
    public static final String ALL_USER = "allUsers";
    public static final String USER_PROFILES = "userProfiles";

    public static final String ROOM_ID = "roomById";
    public static final String ROOMS_BY_CATEGORY = "roomsByCategory";
    public static final String ROOM_SEARCH_RESULTS = "roomSearchResults";
    public static final String ROOMS = "rooms";

    public static final String CATEGORY_ID = "categoryById";
    public static final String CATEGORY_NAME = "categoryByName";
    public static final String ALL_CATEGORIES = "allCategories";

    public static final List<String> CACHES = Arrays.asList(
            USER_ID,
            ALL_USER,
            USER_PROFILES,
            ROOM_ID,
            ROOMS_BY_CATEGORY,
            ROOM_SEARCH_RESULTS,
            ROOMS, CATEGORY_ID,
            CATEGORY_NAME,
            ALL_CATEGORIES);

}
