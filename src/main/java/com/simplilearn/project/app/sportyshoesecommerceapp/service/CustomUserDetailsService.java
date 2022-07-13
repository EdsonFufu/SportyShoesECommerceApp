package com.simplilearn.project.app.sportyshoesecommerceapp.service;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.User;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.UserRole;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;
    private final static String USER_NOT_FOUND_MSG = "User with username %s not found";

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
        // [ROLE_USER, ROLE_ADMIN,..]
        List<String> roleNames = user.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toList());
        log.info("user:{} and role:{}",user.getName(),roleNames);

        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        for (String role : roleNames) {
            // ROLE_USER, ROLE_ADMIN,..
            GrantedAuthority authority = new SimpleGrantedAuthority(role);
            grantedAuthorities.add(authority);
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.isEnabled(), user.getNonExpired(), true, user.isAccountNonLocked(), grantedAuthorities);
    }

    public User getUserByUsername(String username){
        return userRepository.findUserByUsername(username).orElse(null);
    }
}
