package com.example.pdp_project.service;

import com.example.pdp_project.entity.Attachment;
import com.example.pdp_project.repo.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    public Attachment saveAttachment(MultipartFile file) throws IOException {
        Attachment attachment = new Attachment();
        attachment.setOriginalName(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        attachment.setContent(file.getBytes());
        return attachmentRepository.save(attachment);
    }

    public Attachment getAttachmentById(Integer attachmentId) {
        return attachmentRepository.findById(attachmentId).orElse(null);
    }
}
