package com.simplilearn.project.app.sportyshoesecommerceapp.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "USER")
public class User implements Serializable, UserDetails {
    private static final long serialVersionUID = -7110783454320564849L;

    public enum Role {
        ROLE_ADMIN,
        ROLE_USER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<UserRole> userRoles = new ArrayList<>();

    @Builder.Default
    @Column(name = "IS_LOCKED")
    private Boolean locked = false;

    @Builder.Default
    @Column(name = "IS_ENABLED")
    private Boolean enabled = true;

    @Builder.Default
    @Column(name = "IS_NON_EXPIRED")
    private Boolean nonExpired = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(Role.ROLE_USER.name());
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (UserRole userRole : userRoles){
            grantedAuthorities.add(new SimpleGrantedAuthority(userRole.getRole()));
        }
        //return Collections.singleton(grantedAuthorities);
        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return nonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}