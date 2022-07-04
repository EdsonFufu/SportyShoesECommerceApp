package com.simplilearn.project.app.sportyshoesecommerceapp.repository;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Attempts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttemptsRepository extends JpaRepository<Attempts,Long> {
    Optional<Attempts> findAttemptsByUsername(String username);
}
