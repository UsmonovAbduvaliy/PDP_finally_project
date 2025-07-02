package com.example.pdp_project.entity;

import com.example.pdp_project.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public class Message extends BaseEntity {

    @ManyToOne
    private User from;

    @ManyToOne
    private User to;

    private String text;

    private String audioUrl;

    private String fileUrl;

    private String fileName;

    @CreationTimestamp
    private LocalDateTime sentAt;

}

