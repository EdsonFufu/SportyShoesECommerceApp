package com.simplilearn.project.app.sportyshoesecommerceapp.service;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Cart;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Image;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.paging.Paged;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.paging.Paging;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    @Autowired private CartRepository cartRepository;
    public Cart save(Cart cart){
        return cartRepository.save(cart);
    }
    public Cart getCartById(long id){
        return cartRepository.getById(id);
    }

    public Cart getCartSessionId(String sessionId){
        return cartRepository.getCartBySessionId(sessionId);
    }

    public void delete(Cart cart) {
        cartRepository.delete(cart);
    }

    public Paged<Cart> getPage(int pageNumber, int size) {
        PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Cart> cartPage = cartRepository.findAll(request);
        return new Paged<>(cartPage, Paging.of(cartPage.getTotalPages(), pageNumber, size));
    }

    public void deleteById(Long id) {
        cartRepository.deleteById(id);
    }
}
