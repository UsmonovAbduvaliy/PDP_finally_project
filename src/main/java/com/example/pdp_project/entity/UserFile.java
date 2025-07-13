package com.example.pdp_project.entity;

import com.example.pdp_project.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserFile extends BaseEntity {

    private String originalName;
    private String contentType;

    @Lob
    private byte[] data;
}
