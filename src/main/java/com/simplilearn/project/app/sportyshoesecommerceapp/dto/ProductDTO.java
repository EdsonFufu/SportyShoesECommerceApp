package com.simplilearn.project.app.sportyshoesecommerceapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private long id;

    private String name;

    private int number;

    private String color;

    private String size;

    private String price;

    private String brand;

    private int stockLevel;

    private String description;

    private long categoryId;

}