package com.engly.engly_server.models.enums;

import lombok.Getter;

@Getter
public enum CategoryType {
    HOBBIES("Hobbies"),
    TECH("Tech"),
    NEWS("News"),
    GENERAL_CHAT("General Chat"),
    SPORTS("Sports"),
    TRAVEL_AND_FOOD("Travel and Food"),
    CAREER("Career"),
    MOVIES("Movies");

    private final String val;

    CategoryType(String val) {
        this.val = val;
    }
}
