package com.simplilearn.project.app.sportyshoesecommerceapp.mappers;


import org.mapstruct.Builder;

@org.mapstruct.Mapper(componentModel = "spring",builder =  @Builder(disableBuilder = true))
public interface Mapper {
//    @org.mapstruct.Mappings({
//            @Mapping(target = "email",source = "email"),
//            @Mapping(target = "name",source = "fullname"),
//            @Mapping(target = "password",source = "password"),
//            @Mapping(target = "username",source = "username"),
//    })
//    User fromUserDTO(UserDTO userDTO);
}
