package com.example.pdp_project.repo;

import com.example.pdp_project.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
  List<Message> findByFromIdAndToIdOrToIdAndFromIdOrderBySentAtAsc(
          Integer fromId1, Integer toId1,
          Integer toId2,   Integer fromId2
  );
}