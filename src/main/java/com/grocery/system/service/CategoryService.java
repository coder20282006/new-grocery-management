package com.grocery.system.service;

import com.grocery.system.entity.Category;
import com.grocery.system.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findAll() { return categoryRepository.findAll(); }
    public Category findById(Long id) { return categoryRepository.findById(id).orElse(null); }
    public Category save(Category category) { return categoryRepository.save(category); }
    public void deleteById(Long id) { categoryRepository.deleteById(id); }
}
