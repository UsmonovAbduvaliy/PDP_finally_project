package com.example.pdp_project.repo;

import com.example.pdp_project.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (m.from.id = :user1 AND m.to.id = :user2) OR (m.from.id = :user2 AND m.to.id = :user1) ORDER BY m.sentAt ASC")
    List<Message> getChatMessages(@Param("user1") Integer user1, @Param("user2") Integer user2);

}