package com.simplilearn.project.app.sportyshoesecommerceapp.service;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Category;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Image;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Product;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.paging.Paged;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.paging.Paging;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.CategoryRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product save(Product product){
        return productRepository.save(product);
    }

    public Product getProduct(long id) {
        return productRepository.getById(id);
    }

    public List<Product> findAll(){
        return productRepository.findAll();
    }

    public boolean deleteProduct(Long id) {
        try {
            productRepository.deleteById(id);
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }
    public Paged<Product> getPage(int pageNumber, int size) {
        PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Product> productPage = productRepository.findAll(request);
        return new Paged<>(productPage, Paging.of(productPage.getTotalPages(), pageNumber, size));
    }

    public List<Product> getRecentProducts(){
        return productRepository.findTop6ByOrderByCreatedDateDesc();
    }
}
