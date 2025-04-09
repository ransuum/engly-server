package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.request.create.MessageRequest;
import com.engly.engly_server.service.MessageService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    public ResponseEntity<MessagesDto> sendMessage(@RequestBody MessageRequest messageRequest) {
        return new ResponseEntity<>(messageService.sendMessage(messageRequest), HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    public ResponseEntity<MessagesDto> editMessage(@PathVariable String id,
                                                   @NotBlank(message = "Pls input some new message")
                                                   @Size(max = 200)
                                                   @RequestParam String content) {
        return new ResponseEntity<>(messageService.editMessage(id, content), HttpStatus.OK);
    }

    @GetMapping("/current-room/{roomId}")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<PagedModel<EntityModel<MessagesDto>>> findAllMessageInCurrentRoom(@PathVariable String roomId,
                                                                                            @PageableDefault Pageable pageable,
                                                                                            PagedResourcesAssembler<MessagesDto> assembler) {
        var messages = messageService.findAllMessageInCurrentRoom(roomId, pageable);
        return ResponseEntity.ok(assembler.toModel(messages));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    public ResponseEntity<String> deleteMessage(@PathVariable String id) {
        messageService.deleteMessage(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
