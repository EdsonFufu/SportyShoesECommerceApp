package com.simplilearn.project.app.sportyshoesecommerceapp.service;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Cart;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
