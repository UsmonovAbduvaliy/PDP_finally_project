package com.example.pdp_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDto {
    private Long id;
    private Long fromId;
    private Long toId;
    private String content;
    private LocalDateTime sentAt;
    private List<Long> fileIds;
}
