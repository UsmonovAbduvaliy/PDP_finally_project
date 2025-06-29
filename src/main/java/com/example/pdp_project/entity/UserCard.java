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
public class UserCard extends BaseEntity {

    String cardNumber;
    String cardName;
    Integer expiryMonth;
    Integer expiryYear;
    String CVC;

    @ManyToOne
    User user;

}
