package com.engly.engly_server.listeners.models;

import com.engly.engly_server.models.entity.Message;
import lombok.NonNull;

import java.util.List;

public record MessagesViewedEvent(@NonNull List<Message> messages, @NonNull String userId) { }
