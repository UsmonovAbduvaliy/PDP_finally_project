package com.example.pdp_project.repo;

import com.example.pdp_project.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("""
    SELECT m FROM Message m
    WHERE (m.from.id = :user1 AND m.to.id = :user2)
       OR (m.from.id = :user2 AND m.to.id = :user1)
    ORDER BY m.sentAt DESC
""")
    Page<Message> findChatBetweenUsers(@Param("user1") Long user1,
                                       @Param("user2") Long user2,
                                       Pageable pageable);


}