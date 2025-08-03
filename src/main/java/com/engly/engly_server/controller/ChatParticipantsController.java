package com.engly.engly_server.controller;

import com.engly.engly_server.models.enums.RoomRoles;
import com.engly.engly_server.service.common.ChatParticipantsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat-participants")
@Tag(name = "14. Chat participants", description = "API for managing participants in chat")
@RequiredArgsConstructor
public class ChatParticipantsController {

    private final ChatParticipantsService chatParticipantsService;

    @DeleteMapping("/{participantId}")
    @PreAuthorize("hasAuthority('SCOPE_DELETE_GLOBAL')")
    public ResponseEntity<Void> removeParticipant(@PathVariable String participantId) {
        chatParticipantsService.removeParticipant(participantId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-role/{participantId}")
    @PreAuthorize("hasAuthority('SCOPE_UPDATE_GLOBAL')")
    public ResponseEntity<Void> updateParticipantRole(@PathVariable String participantId, @RequestParam RoomRoles role) {
        chatParticipantsService.updateRoleOfParticipant(participantId, role);
        return ResponseEntity.ok().build();
    }
}
