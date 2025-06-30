package com.example.pdp_project.controller;

import com.example.pdp_project.dto.request.MessageDto;
import com.example.pdp_project.entity.Message;
import com.example.pdp_project.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/{user1}/{user2}")
    public ResponseEntity<?> chat(
            @PathVariable Long user1,
            @PathVariable Long user2){
        return ResponseEntity.ok(messageService.chat(user1, user2));
    }

    @PostMapping("/send")
    public ResponseEntity<?> send (@RequestBody MessageDto messageDto){
        Message send = messageService.send(messageDto);
        return ResponseEntity.ok(send);
    }
}
