package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Category;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Image;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.ImageRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.User;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.CategoryService;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.UserService;
import com.simplilearn.project.app.sportyshoesecommerceapp.utils.AuthUtils;
import com.simplilearn.project.app.sportyshoesecommerceapp.utils.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired private UserService userService;

    @Autowired private CategoryService  categoryService;

    @Autowired private ImageRepository imageRepository;

    private final String UPLOAD_DIR = "/uploads/";

    @GetMapping (value = {"/","/index"})
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView("category/index");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        modelAndView.addObject("categories",categoryService.findAll());
        return modelAndView;
    }
    @GetMapping (value = {"{id}"})
    public ModelAndView view(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView();
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }


        modelAndView.setViewName("category/index");
        return modelAndView;
    }
    @GetMapping (value = {"/update/{id}"})
    public ModelAndView edit(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView("category/update");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        modelAndView.addObject("category",categoryService.getCategory(id));
        return modelAndView;
    }
    @GetMapping (value = {"/delete/{id}"})
    public ModelAndView delete(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView("category/index");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.isAuthenticated()){
            modelAndView.addObject("error","Content Available Only for Users with Admin Role");
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }
        if(categoryService.deleteCategory(id)){
            modelAndView.addObject("success","Category Deleted Successfully");
            modelAndView.setViewName("redirect:/category/index");
            return modelAndView;
        }
        modelAndView.addObject("error","Failed to Delete Category [" + categoryService.getCategory(id).getName() + "]");

        return modelAndView;
    }
    @GetMapping (value = {"/create","/add"})
    public ModelAndView create(){
        ModelAndView modelAndView = new ModelAndView("category/create");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.isAuthenticated()){
            modelAndView.addObject("error","Content Available Only for Users with Admin Role");
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }

        modelAndView.addObject("category",new Category());
        return modelAndView;
    }
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PostMapping("/save")
    public ModelAndView add(@RequestParam("file") MultipartFile file,@ModelAttribute("category") Category category, BindingResult bindingResult) throws Exception {
        ModelAndView modelAndView = new ModelAndView("category/create");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        if (bindingResult.hasErrors()){
            modelAndView.addObject("error",bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
            throw new Exception(bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
        }

        if (file.isEmpty()) {
            modelAndView.addObject("error", "Please select a file to upload.");
            return modelAndView;
        }

        // normalize the file path
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());



        String imagePath = FileUploadUtil.saveFile(UPLOAD_DIR, fileName, file);
        // save the file on the local file system
        Image image = imageRepository.save(Image.builder().name(fileName).path(imagePath).size(file.getSize()).build());
        if(image.getId() > 0) {
            category.setImage(image);
            Category newCategory = categoryService.save(category);
            if(newCategory != null){
                modelAndView.addObject("success", "Category Created Successfully");
                return modelAndView;
            }
        }
        modelAndView.addObject("error", "Failed to Upload category.");
        return modelAndView;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PostMapping(value = {"/update"})
    public ModelAndView update(@RequestParam("file") MultipartFile file,@ModelAttribute("category") Category category, BindingResult bindingResult) throws Exception {
        ModelAndView modelAndView = new ModelAndView("redirect:/category/index");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        if (bindingResult.hasErrors()){
            modelAndView.addObject("errorMessage",bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
            throw new Exception(bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
        }

        Image image = null;

        if (!file.isEmpty()) {
            // normalize the file path
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            String imagePath = FileUploadUtil.saveFile(UPLOAD_DIR, fileName, file);
            // save the file on the local file system
            image = imageRepository.save(Image.builder().name(fileName).path(imagePath).size(file.getSize()).build());
        }

        if(image != null) {
            category.setImage(image);
        }
        Category newCategory = categoryService.save(category);
        if(newCategory != null){
            modelAndView.addObject("success", "Category Updated Successfully");
            return modelAndView;
        }
        modelAndView.addObject("error", "Failed to Update category.");
        return modelAndView;
    }

}
