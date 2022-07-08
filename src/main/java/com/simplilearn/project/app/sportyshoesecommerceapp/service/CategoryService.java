package com.simplilearn.project.app.sportyshoesecommerceapp.service;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Category;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category save(Category category){
        return categoryRepository.save(category);
    }

    public Category getCategory(long id) {
        return categoryRepository.getById(id);
    }

    public boolean deleteCategory(long id) {
        try {
            categoryRepository.deleteById(id);
            return true;
        }catch (Exception e){
            log.info(e.getMessage());
            return false;
        }
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
