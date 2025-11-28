package com.example.pdp_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TgUser {
    public TgUser(User user , Long chatId) {
        this.name = user.getName();
        this.chatId = chatId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long chatId;
    private String username;

    private String name;
    private String state = "MENU";
    private Long categoryId;
    private Long selectedCategoryId;
    private Long selectedTripId;
    private Long adSelectedTripId;
    private Double balance = 10000.0;
    private Integer refId;
    private String referralCode;
    private Integer refIdNext;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> deletedMessageIds = new ArrayList<>();
    private Integer deletedMessageId;
    private Integer deletedMessageIdNext;
}
