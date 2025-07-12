package com.example.pdp_project.controller;

import com.example.pdp_project.dto.request.MessageDto;
import com.example.pdp_project.entity.Message;
import com.example.pdp_project.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

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

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> send (@RequestBody MessageDto messageDto,@ModelAttribute List<MultipartFile> files){
        Message send = messageService.send(messageDto,files);
        return ResponseEntity.ok(send);
    }
}
