package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.UserDTO;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.User;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
@Slf4j
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value={ "/login"}, method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            modelAndView.setViewName("login");
            return modelAndView;
        }else {
            return new ModelAndView("redirect:index");
        }

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutSuccessfulPage(Model model, HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        model.addAttribute("title", "Logout");
        return "redirect:/login";
    }


    @RequestMapping(value="/register", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("message", "Register");
        modelAndView.addObject("user", UserDTO.builder().build());
        return modelAndView;
    }

    @PostMapping(
            value = "/register",
            consumes = "application/x-www-form-urlencoded;charset=UTF-8"
            // produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public ModelAndView createNewUser(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByUsername(userDTO.getUsername());
        if (userExists != null) {
            bindingResult
                    .rejectValue("username", "error.user",
                            "There is already a user registered with the user name provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("register");
        } else {
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            log.info(userDTO.toString());
            User user = User.builder()
                    .name(userDTO.getFullname())
                    .username(userDTO.getUsername())
                    .password(userDTO.getPassword())
                    .email(userDTO.getEmail())
                    .build();


            log.info(user.toString());
            user.setLocked(false);

            User registeredUser = userService.createUser(user);
            modelAndView.addObject("user", UserDTO.builder().build());
            if(registeredUser != null) {
                modelAndView.addObject("message", "User [" + registeredUser.getId() + "] has been registered successfully");
                modelAndView.setViewName("login");
            }else {
                modelAndView.addObject("message", "Failed to register User...");
                modelAndView.setViewName("register");
            }


        }
        return modelAndView;
    }

//    @RequestMapping(value="/admin/home", method = RequestMethod.GET)
//    public ModelAndView home(){
//        ModelAndView modelAndView = new ModelAndView();
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User user = userService.findUserByUserName(auth.getName());
//        modelAndView.addObject("userName", "Welcome " + user.getUserName() + "/" + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
//        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
//        modelAndView.setViewName("admin/home");
//        return modelAndView;
//    }


}