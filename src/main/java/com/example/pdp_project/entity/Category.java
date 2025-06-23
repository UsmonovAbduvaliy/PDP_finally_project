package com.example.pdp_project.entity;

import jakarta.persistence.*;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    @OneToOne
    private Attachment attachment;
}

