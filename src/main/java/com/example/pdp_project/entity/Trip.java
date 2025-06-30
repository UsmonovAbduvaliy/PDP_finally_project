package com.example.pdp_project.entity;

import com.example.pdp_project.entity.base.BaseEntity;
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
public class Trip extends BaseEntity {

    private String title;
    private String description;

    private String country;
    private Double rating;
    private Double price;

    @ManyToOne
    private Category category;
    @OneToOne
    private Attachment photo;
}

