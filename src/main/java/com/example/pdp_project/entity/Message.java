package com.example.pdp_project.entity;

import com.example.pdp_project.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public class Message extends BaseEntity {

    @ManyToOne
    private User from;

    @ManyToOne
    private User to;

    private String text;

    @OneToMany(fetch = FetchType.EAGER)
    List<UserFile> files = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime sentAt;

}

