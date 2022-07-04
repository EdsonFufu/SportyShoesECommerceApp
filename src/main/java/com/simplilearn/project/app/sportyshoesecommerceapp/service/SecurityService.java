package com.simplilearn.project.app.sportyshoesecommerceapp.service;

public interface SecurityService {
    boolean isAuthenticated();
    void autoLogin(String username, String password);
}