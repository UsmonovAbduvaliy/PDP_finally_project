package com.example.pdp_project.entity;

import com.example.pdp_project.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Hotel extends BaseEntity {

    private String name;
    private String city;
    private Double pricePerNight;
    private double rating;
    private int reviews;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Attachment> images;

    @ManyToMany
    @JoinTable(name = "hotel_facility",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id"))
    private List<Facility> facilities;
}
