package com.simplilearn.project.app.sportyshoesecommerceapp.dto;

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
    private boolean enabled;
    private boolean locked;

}
