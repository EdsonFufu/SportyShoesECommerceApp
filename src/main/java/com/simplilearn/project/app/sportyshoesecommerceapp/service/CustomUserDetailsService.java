package com.simplilearn.project.app.sportyshoesecommerceapp.service;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.User;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.UserRole;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

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

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        //grantedAuthorities.add(new SimpleGrantedAuthority(user.getUserRole().name()));
        for (UserRole userRole : user.getUserRoles()){
            grantedAuthorities.add(new SimpleGrantedAuthority(userRole.getRole()));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}
