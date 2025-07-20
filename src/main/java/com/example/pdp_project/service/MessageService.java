package com.example.pdp_project.service;

import com.example.pdp_project.dto.request.MessageDto;
import com.example.pdp_project.dto.response.MessageResponseDto;
import com.example.pdp_project.entity.Message;
import com.example.pdp_project.entity.User;
import com.example.pdp_project.entity.UserFile;
import com.example.pdp_project.repo.MessageRepository;
import com.example.pdp_project.repo.UserFileRepository;
import com.example.pdp_project.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserFileRepository userFileRepository;

    public Page<MessageResponseDto> chat(Long user1, Long user2, Pageable pageable) {
        Page<Message> messages = messageRepository.findChatBetweenUsers(user1, user2,pageable);

        return messages.map(message -> {
            List<Long> fileIds = message.getFiles() == null
                    ? Collections.emptyList()
                    : message.getFiles().stream().map(UserFile::getId).toList();

            return new MessageResponseDto(
                    message.getId(),
                    message.getFrom().getId(),
                    message.getTo().getId(),
                    message.getText(),
                    message.getSentAt(),
                    fileIds
            );
        });
    }

    @SneakyThrows
    public Message send(MessageDto dto, List<MultipartFile> files) {

        User userFrom = userRepository.findById(dto.getFrom()).orElseThrow();
        User userTo = userRepository.findById(dto.getTo()).orElseThrow();

        Message message = new Message();
        message.setFrom(userFrom);
        message.setTo(userTo);
        message.setSentAt(LocalDateTime.now());
        message.setText(dto.getContent());
        List<UserFile> userFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                UserFile userFile = getUserFile(file);
                userFiles.add(userFile);
            }
        }
        if (!userFiles.isEmpty()) {
            List<UserFile> savedFiles = userFileRepository.saveAll(userFiles);
            message.setFiles(savedFiles);
        }
        return messageRepository.save(message);
    }

    private static UserFile getUserFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return null;
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Fayl hajmi juda katta. Maksimal ruxsat etilgan: 10 MB");
        }

        return UserFile.builder()
                .originalName(originalFilename)
                .contentType(file.getContentType())
                .data(file.getBytes())
                .build();
    }


}
