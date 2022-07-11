package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.dto.ProductDTO;
import com.simplilearn.project.app.sportyshoesecommerceapp.exceptions.NotFoundException;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Category;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Image;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Product;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.User;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.ImageRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.CategoryService;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.ProductService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired private UserService userService;

    @Autowired private ProductService productService;

    @Autowired private CategoryService categoryService;

    @Autowired private ImageRepository imageRepository;

    private final String UPLOAD_DIR = "/uploads/";

    @GetMapping (value = {"/","/index"})
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView("product/index");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        modelAndView.addObject("products",productService.findAll());
        return modelAndView;
    }
    @GetMapping (value = {"{id}"})
    public ModelAndView view(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView();
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }

        modelAndView.addObject("product",productService.getProduct(id));
        modelAndView.setViewName("category/view");
        return modelAndView;
    }
    @GetMapping (value = {"/update/{id}"})
    public ModelAndView edit(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView("product/update");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        modelAndView.addObject("product",productService.getProduct(id));
        return modelAndView;
    }
    @GetMapping (value = {"/delete/{id}"})
    public ModelAndView delete(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView("product/index");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        if(productService.deleteProduct(id)){
            modelAndView.addObject("success","Product Deleted Successfully");
            modelAndView.setViewName("redirect:/product/index");
            return modelAndView;
        }
        modelAndView.addObject("error","Failed to Delete Product [" + productService.getProduct(id).getName() + "]");
        return modelAndView;
    }
    @GetMapping (value = {"/create","/add"})
    public ModelAndView create(){
        ModelAndView modelAndView = new ModelAndView("product/create");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }

        modelAndView.addObject("categories",categoryService.findAll());
        modelAndView.addObject("product",new ProductDTO());
        return modelAndView;
    }
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PostMapping("/save")
    public ModelAndView add(@RequestParam("files") MultipartFile[] files, @ModelAttribute("product") ProductDTO productDTO, BindingResult bindingResult) throws Exception {
        ModelAndView modelAndView = new ModelAndView("product/create");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        if (bindingResult.hasErrors()){
            modelAndView.addObject("error",bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
            throw new Exception(bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
        }

        if (files.length < 1) {
            modelAndView.addObject("error", "Please select a file to upload.");
            return modelAndView;
        }

        Product product = Product.builder()
                .name(productDTO.getName())
                .number(productDTO.getNumber())
                .description(productDTO.getDescription())
                .brand(productDTO.getBrand())
                .size(productDTO.getSize())
                .color(productDTO.getColor())
                .price(productDTO.getPrice())
                .category(categoryService.getCategory(productDTO.getCategoryId()))
                .build();

        Product newProduct = productService.save(product);

        if(newProduct != null) {

            List<Image> imageList = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();

                String imagePath = FileUploadUtil.saveFile(UPLOAD_DIR, fileName, file);
                // Adding file into fileList
                imageList.add(Image.builder().name(fileName).path(imagePath).size(file.getSize()).product(product).build());
            }

            // Saving all the images to DB
            List<Image> images = imageRepository.saveAll(imageList);

            if (images.size() > 0) {
                modelAndView.addObject("success", "Product Created Successfully");
                return modelAndView;
            }
        }
        modelAndView.addObject("error", "Failed to Upload product.");
        return modelAndView;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PostMapping(value = {"/update"})
    public ModelAndView update(@RequestParam("file") MultipartFile[] files,@ModelAttribute("product") ProductDTO productDTO, BindingResult bindingResult) throws Exception {
        ModelAndView modelAndView = new ModelAndView("redirect:/category/index");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        if (bindingResult.hasErrors()){
            modelAndView.addObject("errorMessage",bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
            throw new Exception(bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
        }


        List<Image> imageList = new ArrayList<>();
        if (files.length > 0) {

            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();

                String imagePath = FileUploadUtil.saveFile(UPLOAD_DIR, fileName, file);
                // Adding file into fileList
                imageList.add(Image.builder().name(fileName).path(imagePath).size(file.getSize()).build());
            }
        }

        // Saving all the images to DB
        List<Image> images = new ArrayList<>();

        if(!imageList.isEmpty()) {
            images = imageRepository.saveAll(imageList);
        }
        Product product = Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .number(productDTO.getNumber())
                .description(productDTO.getDescription())
                .brand(productDTO.getBrand())
                .size(productDTO.getSize())
                .color(productDTO.getColor())
                .price(productDTO.getPrice())
                .category(categoryService.getCategory(productDTO.getCategoryId()))
                .build();
        if(!images.isEmpty()) {
            product.setImages(images);
        }

        Product updateProduct = productService.save(product);
        if(updateProduct != null){
            modelAndView.addObject("success", "Product Updated Successfully");
            return modelAndView;
        }
        modelAndView.addObject("error", "Failed to Update product.");
        return modelAndView;
    }


}
