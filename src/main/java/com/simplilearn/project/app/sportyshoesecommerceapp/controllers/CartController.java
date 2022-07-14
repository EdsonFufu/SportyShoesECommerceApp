package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Image;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.CartRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.ImageRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.CartService;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.ImageService;
import com.simplilearn.project.app.sportyshoesecommerceapp.utils.AuthUtils;
import com.simplilearn.project.app.sportyshoesecommerceapp.utils.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired private CartService cartService;

    @GetMapping (value = {"/","/index"})
    public ModelAndView index(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                              @RequestParam(value = "size", required = false, defaultValue = "6") int size, ModelAndView modelAndView){
        modelAndView.setViewName("cart/index");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        modelAndView.addObject("carts", cartService.getPage(pageNumber, size));
        return modelAndView;
    }
    @GetMapping (value = {"{id}"})
    public ModelAndView view(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView("cart/view");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        modelAndView.addObject("cart",cartService.getCartById(id));
        return modelAndView;
    }

    @GetMapping (value = {"/delete/{id}"})
    public ModelAndView delete(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView("cart/index");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        cartService.deleteById(id);
        modelAndView.addObject("success","Cart Record Deleted Successfully");
        modelAndView.setViewName("redirect:/cart/index");
        return modelAndView;
    }

}
