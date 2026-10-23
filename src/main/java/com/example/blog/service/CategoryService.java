package com.example.blog.service;

import com.example.blog.dto.CategoryRequest;
import com.example.blog.entity.Category;
import com.example.blog.exception.ConflictException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " was not found"));
    }

    public Category create(CategoryRequest request) {
        ensureNameAvailable(request.name(), null);
        return categoryRepository.save(new Category(request.name().trim(), request.description()));
    }

    public Category update(Long id, CategoryRequest request) {
        Category category = findById(id);
        ensureNameAvailable(request.name(), id);
        category.setName(request.name().trim());
        category.setDescription(request.description());
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        categoryRepository.delete(findById(id));
    }

    private void ensureNameAvailable(String name, Long currentId) {
        boolean duplicate = categoryRepository.existsByNameIgnoreCase(name.trim())
                && (currentId == null || !categoryRepository.findById(currentId)
                .map(category -> category.getName().equalsIgnoreCase(name.trim()))
                .orElse(false));
        if (duplicate) {
            throw new ConflictException("A category with that name already exists");
        }
    }
}
