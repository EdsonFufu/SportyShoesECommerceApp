package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.dto.UserDTO;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.Cart;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.User;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.UserRole;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.UserRoleRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.CartService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CartService cartService;

    @RequestMapping(value={ "/login"})
    public ModelAndView login(@RequestParam(value = "error", required = false) boolean error){
        ModelAndView modelAndView = new ModelAndView("login");

        if(error){
            modelAndView.addObject("user",User.builder().build());
            modelAndView.addObject("errorMessage","Login Failed!... Invalid Username OR Password...");
            return modelAndView;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            modelAndView.addObject("user",User.builder().build());
            return modelAndView;
        }else {
            return new ModelAndView("redirect:index");
        }

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutSuccessfulPage(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            Cart cart = cartService.getCartSessionId(session.getId());
            if(!cart.isStatus()){
                cartService.delete(cart);
            }
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
                    .enabled(userDTO.isEnabled())
                    .locked(userDTO.isLocked())
                    .build();


            log.info(user.toString());

            User registeredUser = userService.createUser(user);

            modelAndView.addObject("user", UserDTO.builder().build());
            if(registeredUser != null) {
                userRoleRepository.save(UserRole.builder().user(registeredUser).role(User.Role.ROLE_USER.name()).build());
                modelAndView.addObject("message", "User [" + registeredUser.getId() + "] has been registered successfully");
                modelAndView.setViewName("login");
            }else {
                modelAndView.addObject("message", "Failed to register User...");
                modelAndView.setViewName("register");
            }


        }
        return modelAndView;
    }

    @GetMapping (value = {"/users","/users/index"})
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("user/index");
        modelAndView.addObject("users",userService.getAllUsers());
        return modelAndView;
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public ModelAndView accessDenied(ModelAndView modelAndView) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        modelAndView.setViewName("403");

        if (auth.getPrincipal() != null) {

            modelAndView.addObject("userInfo", auth.getPrincipal());

            String message = "Hi <b>" + auth.getName() + "</b> You do not have permission to access this page!";
            modelAndView.addObject("errorMessage", message);
            modelAndView.addObject("errorCode", "403");

        }

        return modelAndView;
    }




}