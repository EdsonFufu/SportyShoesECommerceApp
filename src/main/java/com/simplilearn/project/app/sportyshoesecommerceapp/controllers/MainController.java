package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/")
public class MainController {
    @Autowired private CustomUserDetailsService userDetailsManager;
    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private CustomUserDetailsService customUserDetailsService;

    //@Autowired private Mapper mapper;

    @GetMapping(value = {"/","/index","/welcome"})
    public ModelAndView index(Model model){
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("message", "Welcome To Spoty Shoe E-Commerce WebApp");
        return modelAndView;
    }

//    @PostMapping("/login")
//    public String GetAuthentication(HttpServletRequest request, Model model,String error, String logout){
//        HttpSession session=request.getSession();
//        ModelAndView modelAndViewLogin = new ModelAndView("login");
//        ModelAndView modelAndView = new ModelAndView("login");
//        String username = request.getParameter("username");
//        String password = request.getParameter("password");
//
//        if (securityService.isAuthenticated()) {
//            return "redirect:/index";
//        }
//
//        if (error != null)
//            model.addAttribute("error", "Your username and password is invalid.");
//
//        if (logout != null)
//            model.addAttribute("message", "You have been logged out successfully.");
//
//        return "login";
//    }

    private String getErrorMessage(HttpServletRequest request, String key) {
        Exception exception = (Exception) request.getSession().getAttribute(key);
        String error = "";
        if (exception instanceof BadCredentialsException) {
            error = "Invalid username and password!";
        } else if (exception instanceof LockedException) {
            error = exception.getMessage();
        } else {
            error = "Invalid username and password!";
        }
        return error;
    }
}
