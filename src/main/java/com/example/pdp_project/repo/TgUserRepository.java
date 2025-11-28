package com.example.pdp_project.repo;

import com.example.pdp_project.entity.TgUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TgUserRepository extends JpaRepository<TgUser, Integer> {

    TgUser findByChatId(Long chatId);
}
