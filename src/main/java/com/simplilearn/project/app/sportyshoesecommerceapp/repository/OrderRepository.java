package com.simplilearn.project.app.sportyshoesecommerceapp.repository;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}