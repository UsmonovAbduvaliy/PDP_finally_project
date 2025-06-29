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

public class Location extends BaseEntity {

    String longitude;
    String latitude;
    String country;
    String address;

    @ManyToOne
    User user;
}
