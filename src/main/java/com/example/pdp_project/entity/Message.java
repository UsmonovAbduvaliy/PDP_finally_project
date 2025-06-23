package com.example.pdp_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

