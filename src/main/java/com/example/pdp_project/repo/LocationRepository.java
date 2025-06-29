package com.example.pdp_project.repo;

import com.example.pdp_project.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}