package com.simplilearn.project.app.sportyshoesecommerceapp.service;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Category;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Product;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.CategoryRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product save(Product product){
        return productRepository.save(product);
    }

    public Product getProduct(long id) {
        return productRepository.getById(id);
    }

    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }
}
