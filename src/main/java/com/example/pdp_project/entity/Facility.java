package com.example.pdp_project.entity;

import com.example.pdp_project.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Facility extends BaseEntity {

    private String name;
    @ManyToOne
    private Attachment attachment;

    @ManyToMany(mappedBy = "facilities")
    @JsonIgnore
    private List<Hotel> hotels;
}
