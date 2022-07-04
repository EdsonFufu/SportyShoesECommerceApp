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
@Table(name = "USER_ROLE")
public class UserRole implements Serializable {
    private static final long serialVersionUID = -9140868227407746488L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "role")
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username",referencedColumnName = "username")
    User user;

}