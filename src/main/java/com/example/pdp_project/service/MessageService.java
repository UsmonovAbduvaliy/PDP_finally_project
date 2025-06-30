package com.example.pdp_project.service;

import com.example.pdp_project.dto.request.MessageDto;
import com.example.pdp_project.entity.Message;
import com.example.pdp_project.entity.User;
import com.example.pdp_project.repo.MessageRepository;
import com.example.pdp_project.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public List<Message> chat(Long user1, Long user2){
        return messageRepository.getChatMessages(user1, user2);
    }

    public Message send(MessageDto dto){
        User userFrom = userRepository.findById(dto.getFrom()).orElseThrow();
        User userTo = userRepository.findById(dto.getTo()).orElseThrow();

       return messageRepository.save(
                Message.builder()
                        .from(userFrom)
                        .to(userTo)
                        .text(dto.getContent())
                        .sentAt(LocalDateTime.now())
                        .build()
        );
    }
}
