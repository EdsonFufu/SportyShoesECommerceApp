package com.simplilearn.project.app.sportyshoesecommerceapp.repository;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findTop6ByOrderByCreatedDateDesc();
}