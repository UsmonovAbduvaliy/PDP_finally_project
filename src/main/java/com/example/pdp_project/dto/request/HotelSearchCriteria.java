package com.example.pdp_project.dto.request;



import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record HotelSearchCriteria(
    @NotBlank
    String city,
    @NotNull
    LocalDate checkIn,
    @NotNull
    LocalDate checkOut,
    @Min(1) Integer guests,
    @Min(1) Integer rooms) {}
