package com.simplilearn.project.app.sportyshoesecommerceapp.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.ModelAndView;
@Slf4j
public class AuthUtils {
    public static ModelAndView isAuthenticated(Authentication authentication, ModelAndView modelAndView){
        if(authentication.isAuthenticated()){
            log.info("Welcome [" + authentication.getName() +"]");
            return modelAndView;
        }
        modelAndView.addObject("error","You have not logged in....");
        modelAndView.setViewName("redirect:/login");
        return modelAndView;
    }
}
