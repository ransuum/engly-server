package com.engly.engly_server.exception.handler;

public class ExceptionMessage {

    private ExceptionMessage() {
        // Private constructor to prevent instantiation
    }

    public static final String USER_SETTINGS_NOT_FOUND = "UserSettings not found";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_NOT_FOUND_BY_ID = "User not found with id: %s";
    public static final String USER_NOT_FOUND_BY_EMAIL = "User not found with email: %s";
    public static final String ROOM_NOT_FOUND = "Room not found";
    public static final String ROOM_ALREADY_EXISTS = "Room with this name already exists";
    public static final String PROFILE_NOT_FOUND = "Cannot found this profile";
    public static final String MESSAGE_NOT_FOUND = "Message not found with id: %s";
    public static final String PARTICIPANT_NOT_FOUND = "Chat participant not found with id: %s";
    public static final String CATEGORY_NOT_FOUND_MESSAGE = "Category with id %s not found";
    public static final String CATEGORY_ALREADY_EXISTS = "Category with name '%s' already exists";
    public static final String INVALID_CREDENTIALS = "Invalid credentials provided";
    public static final String AUTHENTICATION_OBJECT_NOT_FOUND = "Authentication object was not found in context";
    public static final String NO_AUTHENTICATED_USER_FOUND = "No authenticated user found";
}
