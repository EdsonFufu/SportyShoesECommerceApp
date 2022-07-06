package com.simplilearn.project.app.sportyshoesecommerceapp.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserDTO {
    private String email;
    private String username;
    private String password;
    private String fullname;
}