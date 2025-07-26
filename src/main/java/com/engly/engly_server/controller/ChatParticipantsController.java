package com.engly.engly_server.controller;

import com.engly.engly_server.models.enums.RoomRoles;
import com.engly.engly_server.service.common.ChatParticipantsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat-participants")
@RequiredArgsConstructor
public class ChatParticipantsController {

    private final ChatParticipantsService chatParticipantsService;

    @DeleteMapping("/{participantId}")
    @PreAuthorize("hasAuthority('SCOPE_DELETE_GLOBAL')")
    public void removeParticipant(@PathVariable String participantId) {
        chatParticipantsService.removeParticipant(participantId);
    }

    @PatchMapping("/update-role/{participantId}")
    @PreAuthorize("hasAuthority('SCOPE_UPDATE_GLOBAL')")
    public void updateParticipantRole(@PathVariable String participantId, @RequestParam RoomRoles role) {
        chatParticipantsService.updateRoleOfParticipant(participantId, role);
    }
}
