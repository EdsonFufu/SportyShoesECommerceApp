package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.exceptions.NotFoundException;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Product;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.User;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.ProductService;
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
@RequestMapping("/product")
public class ProductController {
    @Autowired private UserService userService;
    @Autowired private ProductService  productService;

    @GetMapping (value = {"/","/index"})
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userService.findUserByUsername(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getUsername() + "/" + user.getName() +  " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("product/index");
        modelAndView.addObject("products",productService.getAllProduct());
        return modelAndView;
    }
    @GetMapping (value = {"{id}"})
    public ModelAndView view(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userService.findUserByUsername(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getUsername() + "/" + user.getName() +  " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("product/index");
        return modelAndView;
    }

    @PostMapping(value = {"/"})
    public ModelAndView add(@ModelAttribute("Product") Product product, BindingResult bindingResult) throws Exception {
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

        Product newProduct = productService.save(product);

        log.info(newProduct.toString());


        User user = userService.findUserByUsername(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getUsername() + "/" + user.getName() +  " (" + user.getEmail() + ")");
        modelAndView.addObject("infoMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("product/index");
        return modelAndView;
    }

    public ModelAndView updateView(@PathVariable("id") Long id) throws Exception, NotFoundException {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.isAuthenticated()){
            modelAndView.addObject("errorMessage","Content Available Only for Users with Admin Role");
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }
        Product product = productService.getProduct(id);
        if(product == null || product.getStockLevel() < 1){
            throw new NotFoundException("Product with ID [" + id + "] not found in store");
        }

        modelAndView.addObject("product", product);


        User user = userService.findUserByUsername(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getUsername() + "/" + user.getName() +  " (" + user.getEmail() + ")");
        modelAndView.addObject("infoMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("product/update");
        return modelAndView;
    }

    @PutMapping(value = {"/"})
    public ModelAndView update(@ModelAttribute("product") Product product, BindingResult bindingResult) throws Exception {
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

        Product newProduct = productService.save(product);

        log.info(newProduct.toString());


        User user = userService.findUserByUsername(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getUsername() + "/" + user.getName() +  " (" + user.getEmail() + ")");
        modelAndView.addObject("infoMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("product/index");
        return modelAndView;
    }

}
