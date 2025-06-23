package com.example.pdp_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String cardNumber;
    String cardName;
    Integer expiryMonth;
    Integer expiryYear;
    String CVC;
    @ManyToOne
    User user;

}
