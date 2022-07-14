package com.simplilearn.project.app.sportyshoesecommerceapp.service;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.CartItem;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

    @Autowired private CartItemRepository cartItemRepository;
    public CartItem save(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }
}
