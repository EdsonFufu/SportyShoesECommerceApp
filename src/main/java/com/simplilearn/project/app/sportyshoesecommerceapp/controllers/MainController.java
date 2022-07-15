package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.*;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.CartItemService;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.CartService;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.CustomUserDetailsService;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.ProductService;
import com.simplilearn.project.app.sportyshoesecommerceapp.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
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
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/")
public class MainController {

    @Autowired private ProductService productService;
    @Autowired private CartItemService cartItemService;
    @Autowired private CartService cartService;

    @Autowired private CustomUserDetailsService customUserDetailsService;

    @GetMapping(value = {"/","/index","/welcome"})
    public ModelAndView index(ModelAndView modelAndView){
        modelAndView = new ModelAndView("index");
        modelAndView.addObject("message", "Welcome To Spoty Shoe E-Commerce WebApp");
        List<Product> productList = productService.getRecentProducts();
        modelAndView.addObject("products",ListUtils.partition(productList, 3));
         return modelAndView;
    }

    @GetMapping (value = {"/products","/products/{keyword}/{field}"})
    public ModelAndView products(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                              @RequestParam(value = "size", required = false, defaultValue = "6") int size, ModelAndView modelAndView, @PathVariable(name = "keyword",required = false) String keyword,@PathVariable(name = "field",required = false) String field){
        modelAndView.setViewName("products");
        modelAndView.addObject("products", productService.getPage(pageNumber, size));
        return modelAndView;
    }

    @GetMapping (value = {"/cart-detail"})
    public ModelAndView cartDetail(ModelAndView modelAndView,HttpSession httpSession){
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        modelAndView.setViewName("cart-detail");
        if(!httpSession.isNew()){
            Cart cart = cartService.getCartSessionId(httpSession.getId());
            Double totalAmount = cart.getCartItemList().stream().map(cartItem -> cartItem.getPrice() * cartItem.getQuantity()).reduce(Double::sum).orElse(0.00);
            modelAndView.addObject("cartItems", cart.getCartItemList());
            modelAndView.addObject("cart", cart);
            modelAndView.addObject("cartItemsTotal", totalAmount);
        }
        return modelAndView;
    }

    @PostMapping(value = {"/checkout"})
    public ModelAndView checkout(@ModelAttribute("cart") Cart cart,ModelAndView modelAndView,HttpSession httpSession){
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        User user = customUserDetailsService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        modelAndView.setViewName("order");


        if(!httpSession.isNew()){
            cart = cartService.getCartSessionId(httpSession.getId());
            Double totalAmount = cart.getCartItemList().stream().map(cartItem -> cartItem.getPrice() * cartItem.getQuantity()).reduce(Double::sum).orElse(0.00);
//            Order order = Order.builder().user(user).cart(cart).tax(18.00).subTotal(totalAmount).total(18.00 * totalAmount).grandTotal(18.00 * totalAmount).sessionId(httpSession.getId()).build();
            //modelAndView.addObject("order",Order.builder().build());
        }
        return modelAndView;
    }

    @GetMapping(value = {"/products/{id}"})
    public ModelAndView productsDetail(@PathVariable("id") long id, HttpSession session,ModelAndView modelAndView){
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        modelAndView = new ModelAndView("product-detail");
        Product product = productService.getProduct(id);
        User user = customUserDetailsService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Cart cart;
        if(!session.isNew()){
            cart = cartService.getCartSessionId(session.getId());
        }else {
             cart = cartService.save(Cart.builder()
                    .user(user)
                    .sessionId(session.getId())
                    .status(false)
                    .fullname(user.getName())
                    //.mobile(userInfo.getContact().getMobile())
                    .email(user.getEmail())
                    //.address1(userInfo.getContact().getAddress1())
                    //.address2(userInfo.getContact().getAddress2())
                    //.city(userInfo.getContact().getCity())
                    //.country(userInfo.getContact().getCountry())
                    .build());
        }
        modelAndView.addObject("product", productService.getProduct(id));
        modelAndView.addObject("cartItem", CartItem.builder().cart(cart).product(product).price(Float.parseFloat(product.getPrice())).quantity(1).build());
        return modelAndView;
    }

    @PostMapping(value = {"/cart-item/add/{id}"})
    public ModelAndView addToCart(@ModelAttribute("cartItem") CartItem cartItem,@PathVariable("id") long  productId,  HttpSession session){
        ModelAndView modelAndView = new ModelAndView("redirect:/products");
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        log.info(cartItem.toString());

        int totalItems = 0;

        User user = customUserDetailsService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!session.isNew()){
            totalItems = Integer.parseInt(session.getAttribute("totalItem").toString());
            cartItem.setCart(cartService.getCartSessionId(session.getId()));
        }else {
            cartItem.setCart(cartService.save(Cart.builder()
                    .user(user)
                    .sessionId(session.getId())
                    .status(false)
                    .fullname(user.getName())
                    //.mobile(userInfo.getContact().getMobile())
                    .email(user.getEmail())
                    //.address1(userInfo.getContact().getAddress1())
                    //.address2(userInfo.getContact().getAddress2())
                    //.city(userInfo.getContact().getCity())
                    //.country(userInfo.getContact().getCountry())
                    .build()));
        }
        cartItem.setProduct(productService.getProduct(productId));

        log.info(cartItem.toString());
        System.out.println(cartItem);

        if(cartItemService.save(cartItem) != null){
            session.setAttribute("totalItem",totalItems + 1);
        }

        return modelAndView;
    }
}
