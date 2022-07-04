package com.simplilearn.project.app.sportyshoesecommerceapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SportyShoesECommerceAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportyShoesECommerceAppApplication.class, args);
    }

    @Bean
    @Lazy
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
