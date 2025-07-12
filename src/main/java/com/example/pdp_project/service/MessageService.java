package com.example.pdp_project.service;

import com.example.pdp_project.dto.request.MessageDto;
import com.example.pdp_project.entity.Message;
import com.example.pdp_project.entity.User;
import com.example.pdp_project.entity.UserFile;
import com.example.pdp_project.repo.MessageRepository;
import com.example.pdp_project.repo.UserFileRepository;
import com.example.pdp_project.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserFileRepository userFileRepository;

    public List<Message> chat(Long user1, Long user2){
        return messageRepository.getChatMessages(user1, user2);
    }

    @SneakyThrows
    public Message send(MessageDto dto, List<MultipartFile> files){

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

        return message;
    }

    private static UserFile getUserFile(MultipartFile file) throws IOException {
        UserFile userFile = new UserFile();
        if (file.getOriginalFilename().endsWith(".jpg")|| file.getOriginalFilename().endsWith(".png")) {
            userFile.setImageData(file.getBytes());
        }else if (file.getOriginalFilename().endsWith(".mp3") || file.getOriginalFilename().endsWith(".wav")) {
            userFile.setAudioData(file.getBytes());
        } else if (file.getOriginalFilename().endsWith(".pdf")|| file.getOriginalFilename().endsWith(".docx")) {
            userFile.setFileData(file.getBytes());
        }else{
            userFile.setAnotherData(file.getBytes());
        }
        return userFile;
    }
}
