package com.example.pdp_project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass

public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    byte[] content;
}
