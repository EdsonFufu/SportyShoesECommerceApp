package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Category;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.User;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.CategoryService;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired private UserService userService;

    @Autowired private CategoryService  categoryService;

    @GetMapping (value = {"/","/index"})
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userService.findUserByUsername(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getUsername() + "/" + user.getName() +  " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("caregory/index");
        return modelAndView;
    }
    @GetMapping (value = {"{id}"})
    public ModelAndView view(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userService.findUserByUsername(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getUsername() + "/" + user.getName() +  " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("caregory/index");
        return modelAndView;
    }

    @PostMapping(value = {"/"})
    public ModelAndView add(@ModelAttribute("category") Category category, BindingResult bindingResult) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.isAuthenticated()){
            modelAndView.addObject("errorMessage","Content Available Only for Users with Admin Role");
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }
        if (bindingResult.hasErrors()){
            modelAndView.addObject("errorMessage",bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
            throw new Exception(bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
        }

        Category newCategory = categoryService.save(category);

        log.info(newCategory.toString());


        User user = userService.findUserByUsername(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getUsername() + "/" + user.getName() +  " (" + user.getEmail() + ")");
        modelAndView.addObject("infoMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("caregory/index");
        return modelAndView;
    }

    @PutMapping(value = {"/"})
    public ModelAndView update(@ModelAttribute("category") Category category, BindingResult bindingResult) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.isAuthenticated()){
            modelAndView.addObject("errorMessage","Content Available Only for Users with Admin Role");
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }
        if (bindingResult.hasErrors()){
            modelAndView.addObject("errorMessage",bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
            throw new Exception(bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
        }

        Category newCategory = categoryService.save(category);

        log.info(newCategory.toString());


        User user = userService.findUserByUsername(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getUsername() + "/" + user.getName() +  " (" + user.getEmail() + ")");
        modelAndView.addObject("infoMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("caregory/index");
        return modelAndView;
    }

}
