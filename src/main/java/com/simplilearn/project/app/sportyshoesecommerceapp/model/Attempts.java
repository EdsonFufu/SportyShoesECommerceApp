package com.simplilearn.project.app.sportyshoesecommerceapp.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "attempts")
public class Attempts implements Serializable {
    private static final long serialVersionUID = -5803109412253607205L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "ATTEMPTS", nullable = false)
    private int attempts;

}