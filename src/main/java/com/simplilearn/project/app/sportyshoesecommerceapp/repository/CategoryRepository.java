package com.simplilearn.project.app.sportyshoesecommerceapp.repository;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    long countByNameIgnoreCase(String name);

    boolean existsByName(String name);
}