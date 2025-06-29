package com.example.pdp_project.repo;

import com.example.pdp_project.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}