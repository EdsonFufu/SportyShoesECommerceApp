package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.service.CustomUserDetailsService;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.ProductService;
import com.simplilearn.project.app.sportyshoesecommerceapp.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired private ProductService productService;

    @GetMapping(value = {"/","/index","/welcome"})
    public ModelAndView index(Model model){
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("message", "Welcome To Spoty Shoe E-Commerce WebApp");
        return modelAndView;
    }

    @GetMapping (value = {"/products"})
    public ModelAndView products(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                              @RequestParam(value = "size", required = false, defaultValue = "6") int size, ModelAndView modelAndView){
        modelAndView.setViewName("products");
        modelAndView.addObject("products", productService.getPage(pageNumber, size));
        return modelAndView;
    }

    @GetMapping(value = {"/products/{id}"})
    public ModelAndView productsDetail(@PathVariable("id") long id){
        ModelAndView modelAndView = new ModelAndView("product-detail");
        modelAndView.addObject("product", productService.getProduct(id));
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
