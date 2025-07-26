package com.example.pdp_project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripDTO {
    private String title;
    private String description;
    private String country;
    private Double rating;
    private Double price;
    private Long categoryId;
    private Integer attachmentId;
}
