package com.simplilearn.project.app.sportyshoesecommerceapp.service;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Order;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Setting;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.OrderRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.SettingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OrderService {
    @Autowired private OrderRepository orderRepository;

    public Order save(Order order){
        return orderRepository.save(order);
    }
    public Order getById(long id){
        return orderRepository.getById(id);
    }

    public List<Order> findAll(){
        return orderRepository.findAll();
    }

    public boolean delete(Long id) {
        try {
            orderRepository.deleteById(id);
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }
}
