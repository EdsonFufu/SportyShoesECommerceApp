package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Category;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Image;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.User;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.ImageRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.CategoryService;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.ImageService;
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

import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/image")
public class ImageController {

    @Autowired private ImageRepository imageRepository;
    @Autowired private ImageService imageService;

    private final String UPLOAD_DIR = "./uploads/";

    @GetMapping (value = {"/","/index"})
    public ModelAndView index(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                              @RequestParam(value = "size", required = false, defaultValue = "6") int size, ModelAndView modelAndView){
        modelAndView.setViewName("image/index");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        modelAndView.addObject("images", imageService.getPage(pageNumber, size));
        return modelAndView;
    }
    @GetMapping (value = {"{id}"})
    public ModelAndView view(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView("image/view");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        return modelAndView;
    }
    @GetMapping (value = {"/update/{id}"})
    public ModelAndView edit(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView("category/update");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        modelAndView.addObject("category",imageRepository.getById(id));
        return modelAndView;
    }
    @GetMapping (value = {"/delete/{id}"})
    public ModelAndView delete(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView("image/index");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        imageRepository.deleteById(id);
        modelAndView.addObject("success","Image Deleted Successfully");
        modelAndView.setViewName("redirect:/image/index");
        return modelAndView;
    }
    @GetMapping (value = {"/create","/add"})
    public ModelAndView create(){
        ModelAndView modelAndView = new ModelAndView("image/create");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        return modelAndView;
    }
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PostMapping("/save")
    public ModelAndView add(@RequestParam("file") MultipartFile file, BindingResult bindingResult) throws Exception {
        ModelAndView modelAndView = new ModelAndView("image/create");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        if (bindingResult.hasErrors()){
            modelAndView.addObject("errorMessage",bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
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
            modelAndView.addObject("success", "Image Created Successfully");
            return modelAndView;
        }
        modelAndView.addObject("error", "Failed to Upload image.");
        return modelAndView;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PutMapping(value = {"/"})
    public ModelAndView update(@RequestParam("file") MultipartFile file, BindingResult bindingResult) throws Exception {
        ModelAndView modelAndView = new ModelAndView("image/index");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        if (bindingResult.hasErrors()){
            modelAndView.addObject("error",bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
            throw new Exception(bindingResult.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(":")));
        }

        Image image = null;

        if (!file.isEmpty()) {
            // normalize the file path
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            String imagePath = FileUploadUtil.saveFile(UPLOAD_DIR, fileName, file);
            // save the file on the local file system
            image = imageRepository.save(Image.builder().name(fileName).path(imagePath).build());
            if(image != null){
                modelAndView.addObject("success", "Image Created Successfully");
                return modelAndView;
            }
        }

        modelAndView.addObject("error", "Failed to Upload image.");
        return modelAndView;
    }

}
