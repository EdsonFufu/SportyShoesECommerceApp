package com.simplilearn.project.app.sportyshoesecommerceapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    private String paymentMethod;

    private String paymentAmount;

    private String creditCard;

    private String nameOnCard;

    private String expireDate;

    private String cvv;

    private String paypalEmail;

    private String firstName;

    private String middleName;

    private String lastName;

    private String mobile;

    private String email;

    private String address;

    private String city;

    private String country;
}
