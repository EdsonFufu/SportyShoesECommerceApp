package com.simplilearn.project.app.sportyshoesecommerceapp.controllers;

import com.simplilearn.project.app.sportyshoesecommerceapp.dto.PaymentDTO;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.*;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.ContactRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.PaymentRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.*;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/")
public class MainController {

    @Autowired private ProductService productService;
    @Autowired private CartItemService cartItemService;
    @Autowired private CartService cartService;

    @Autowired private SettingService settingService;

    @Autowired private UserService userService;

    @Autowired private CustomUserDetailsService customUserDetailsService;

    @Autowired private ContactRepository contactRepository;

    @Autowired private PaymentRepository paymentRepository;

    @Autowired private OrderService orderService;

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

        Setting setting = settingService.findAll().stream().limit(1).findFirst().orElse(null);

        if(!httpSession.isNew()){
            if(setting != null) {
                cart = cartService.getCartSessionId(httpSession.getId());
                double totalAmount = cart.getCartItemList().stream().map(cartItem -> cartItem.getPrice() * cartItem.getQuantity()).reduce(Double::sum).orElse(0.00);
                int totalItems = cart.getCartItemList().stream().map(CartItem::getQuantity).reduce(0,Integer::sum);
                Order order = Order.builder().user(user).cart(cart).tax(setting.getVat()).subTotal(totalAmount).total(setting.getVat() * totalAmount).grandTotal(setting.getVat() * totalAmount).sessionId(httpSession.getId()).build();
                modelAndView.addObject("order", order);
                modelAndView.addObject("shipping", setting.getShipping());
                modelAndView.addObject("quatity", totalItems);
                modelAndView.addObject("payment", new PaymentDTO());
            }
        }
        return modelAndView;
    }

    @PostMapping(value = {"/payment"})
    public ModelAndView payment(@ModelAttribute("payment") PaymentDTO paymentDTO,ModelAndView modelAndView,HttpSession httpSession){
        AuthUtils.isAuthenticated(SecurityContextHolder.getContext().getAuthentication(), modelAndView);
        if(modelAndView.getModel().containsKey("error")){
            return modelAndView;
        }
        User user = customUserDetailsService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());




        if(!httpSession.isNew()){
            Setting setting = settingService.findAll().stream().limit(1).findFirst().orElse(null);
            if(setting != null) {

                Contact contact = Contact.builder()
                        .firstName(paymentDTO.getFirstName()).middleName(paymentDTO.getMiddleName()).lastName(paymentDTO.getLastName())
                        .mobile(paymentDTO.getMobile()).email(paymentDTO.getEmail()).city(paymentDTO.getCity()).country(paymentDTO.getCountry())
                        .address1(paymentDTO.getAddress())
                        .build();
                Contact contactSaved = contactRepository.save(contact);
                user.setContact(contactSaved);

                User withNewContact = userService.save(user);

                Cart cart = cartService.getCartSessionId(httpSession.getId());


                double totalAmount = cart.getCartItemList().stream().map(cartItem -> cartItem.getPrice() * cartItem.getQuantity()).reduce(Double::sum).orElse(0.00);

                Order order = Order.builder().user(withNewContact).cart(cart).tax(setting.getVat()/100 * totalAmount).subTotal(totalAmount).total((setting.getVat()/100 * totalAmount) + totalAmount).grandTotal((setting.getVat()/100 * totalAmount) + totalAmount + setting.getShipping()).shipping(setting.getShipping()).sessionId(httpSession.getId()).build();

                Order savedOrder = orderService.save(order);

                //Deduct Order amount to the API based on the chosen payment method

                Payment payment = Payment.builder()
                        .paymentAmount(paymentDTO.getPaymentAmount())
                        .paymentMethod(paymentDTO.getPaymentMethod())
                        .paymentStatus("SUCCESS")
                        .transactionId("99" + String.format("05%d",order.getId()) + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmssSSS")))
                        .order(savedOrder)
                        .build();

                Payment savedPayment = paymentRepository.save(payment);
                if(savedPayment != null) {
                    httpSession.setAttribute("totalItem",0);
                    savedOrder.setPayment(savedPayment);

                    modelAndView.addObject("order", savedOrder);
                    modelAndView.addObject("contact", withNewContact.getContact());
                    modelAndView.addObject("setting", setting);
                    modelAndView.setViewName("invoice");
                }else {
                    modelAndView.setViewName("redirect:/cart-detail");
                }
            }
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
