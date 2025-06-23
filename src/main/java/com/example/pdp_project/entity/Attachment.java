package com.example.pdp_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    byte[] content;
}
