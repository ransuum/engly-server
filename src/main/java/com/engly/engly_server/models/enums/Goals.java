package com.engly.engly_server.models.enums;

import com.engly.engly_server.exception.NotFoundException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Goals {
    DEFAULT("Default"),
    IMPROVE_ENGLISH("Improve English"),
    LEARN_NEW_LANGUAGE("Learn new language"),
    MEET_NEW_PEOPLE("Meet new people");

    private final String label;

    Goals(String label) {
        this.label = label;
    }

    public static Goals fromLabel(String label) {
        return Arrays.stream(Goals.values())
                .filter(goals -> goals.getLabel().equals(label))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("This type of goal is not found"));
    }
}
