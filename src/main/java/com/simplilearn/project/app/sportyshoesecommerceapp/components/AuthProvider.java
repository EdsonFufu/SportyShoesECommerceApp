package com.simplilearn.project.app.sportyshoesecommerceapp.components;

import java.util.Optional;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Attempts;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.User;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.AttemptsRepository;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthProvider implements AuthenticationProvider {
   private static final int ATTEMPTS_LIMIT = 3;
   @Autowired private AttemptsRepository attemptsRepository;
   @Autowired private UserRepository userRepository;
   @Override
   public Authentication authenticate(Authentication authentication) throws AuthenticationException {
      String username = authentication.getName();
      Optional<Attempts>
      userAttempts = attemptsRepository.findAttemptsByUsername(username);
      if (userAttempts.isPresent()) {
         Attempts attempts = userAttempts.get();
         attempts.setAttempts(0);
         attemptsRepository.save(attempts);
      }
      return authentication;
   }
   private void processFailedAttempts(String username, User user) {
      Optional<Attempts>
      userAttempts = attemptsRepository.findAttemptsByUsername(username);
      if (userAttempts.isEmpty()) {
         Attempts attempts = new Attempts();
         attempts.setUsername(username);
         attempts.setAttempts(1);
         attemptsRepository.save(attempts);
      } else {
         Attempts attempts = userAttempts.get();
         attempts.setAttempts(attempts.getAttempts() + 1);
         attemptsRepository.save(attempts);

         if (attempts.getAttempts() + 1 >
         ATTEMPTS_LIMIT) {
            user.setLocked(false);
            userRepository.save(user);
            throw new LockedException("Too many invalid attempts. Account is locked!!");
         }
      }
   }
   @Override public boolean supports(Class<?> authentication) {
      return true;
   }
}