package com.example.pdp_project.service;

import com.example.pdp_project.entity.Category;
import com.example.pdp_project.repo.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repo;
    public List<Category> getAll() {
        return repo.findAll();
    }
}
