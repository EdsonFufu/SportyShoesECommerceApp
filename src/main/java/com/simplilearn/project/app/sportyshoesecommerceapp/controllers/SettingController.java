package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Category;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Image;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Setting;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.ImageRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.CategoryService;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.SettingService;
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
@RequestMapping("/setting")
public class SettingController {
    @Autowired private UserService userService;

    @Autowired private SettingService settingService;

    @GetMapping (value = {"/","/index"})
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView("setting/index");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        modelAndView.addObject("settings",settingService.findAll());
        return modelAndView;
    }
    @GetMapping (value = {"/delete/{id}"})
    public ModelAndView view(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView();
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        if(settingService.delete(id)){
            modelAndView.addObject("info","An Item has been deleted");
        }
        modelAndView.addObject("error","Failed to delete an item");
        modelAndView.setViewName("redirect:/setting/index");
        modelAndView.addObject("settings",settingService.findAll());
        return modelAndView;
    }

    @GetMapping (value = {"/create","/add"})
    public ModelAndView create(){
        ModelAndView modelAndView = new ModelAndView("setting/create");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        modelAndView.addObject("setting",new Setting());
        return modelAndView;
    }
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PostMapping("/save")
    public ModelAndView add(@ModelAttribute("setting") Setting setting) throws Exception {
        ModelAndView modelAndView = new ModelAndView("setting/create");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }

        Setting savedSetting= settingService.save(setting);
        if(savedSetting != null){
            modelAndView.addObject("success", "Record Created Successfully");
            return modelAndView;
        }
        modelAndView.addObject("error", "Failed to Save record.");
        return modelAndView;
    }

}
