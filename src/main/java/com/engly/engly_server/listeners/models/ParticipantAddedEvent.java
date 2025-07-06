package com.engly.engly_server.listeners.models;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Roles;

public record ParticipantAddedEvent(Rooms rooms, Users users, Roles roles) { }
