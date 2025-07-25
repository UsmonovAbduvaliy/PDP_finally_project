package com.example.pdp_project.controller;

import com.example.pdp_project.dto.request.MessageDto;
import com.example.pdp_project.dto.response.MessageResponseDto;
import com.example.pdp_project.entity.Message;
import com.example.pdp_project.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/chat")
    public ResponseEntity<?> getChatMessages(
            @RequestParam Long user1,
            @RequestParam Long user2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        Page<MessageResponseDto> chatDtos = messageService.chat(user1, user2, pageable);

        if (chatDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(chatDtos);
    }

    @PostMapping("/send-text")
    public ResponseEntity<?> sendTextMessage(@RequestBody MessageDto messageDto) {
        Message message = messageService.send(messageDto, Collections.emptyList());
        return ResponseEntity.ok(message);
    }

    @PostMapping(value = "/message/{from}/{to}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> sendFileMessage(
            @PathVariable Long from,
            @PathVariable Long to,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        MessageDto dto = new MessageDto(from,to,"");

        Message message = messageService.send(dto, files == null ? Collections.emptyList() : files);
        return ResponseEntity.ok(message);
    }


}
