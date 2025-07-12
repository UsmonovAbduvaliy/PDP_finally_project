package com.example.pdp_project.repo;

import com.example.pdp_project.entity.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFileRepository extends JpaRepository<UserFile, Long> {
}