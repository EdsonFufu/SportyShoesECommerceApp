package com.simplilearn.project.app.sportyshoesecommerceapp.config;

import com.simplilearn.project.app.sportyshoesecommerceapp.exceptions.CustomAccessDeniedHandler;
import com.simplilearn.project.app.sportyshoesecommerceapp.exceptions.CustomAuthenticationFailureHandler;
import com.simplilearn.project.app.sportyshoesecommerceapp.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public final CustomUserDetailsService customUserDetailsService;

    public final PasswordEncoder passwordEncoder;

//    public final DataSource dataSource;


//    @Bean
//    @Lazy
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(customUserDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }

//    @Override
//    @Autowired
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authenticationProvider());
//    }

    @Override
    @Bean
    @Lazy
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    @Lazy
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
    }


    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/resources/**", "/static/**","/webjars/**","/css/**","/js/**","/images/**","/error");
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http.authorizeRequests()
                .antMatchers("/","/index","/login","/register","/products","/logout").permitAll()
                .antMatchers("/product","/users","/category","/payment","/address","/order","/product/**","/category/**","/users/**","/address/**","/order/**","/payment/**").hasRole("ADMIN").anyRequest().authenticated()
                .and()
                .formLogin(form -> form
                        .loginPage("/login").usernameParameter("username").passwordParameter("password")
                        .defaultSuccessUrl("/")
                        .failureHandler(new AuthenticationFailureHandler() {

                            @Override
                            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                                String email = request.getParameter("username");
                                String error = exception.getMessage();
                                log.info("A failed login attempt with email: "
                                        + email + ". Reason: " + error);

                                String redirectUrl = request.getContextPath() + "/login?error=true";
                                response.sendRedirect(redirectUrl);
                            }
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout().logoutUrl("/logout").logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/").deleteCookies("JSESSIONID").invalidateHttpSession(true)
                .and()
                    .exceptionHandling().accessDeniedPage("/403")
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                    .csrf().disable();

    }


}