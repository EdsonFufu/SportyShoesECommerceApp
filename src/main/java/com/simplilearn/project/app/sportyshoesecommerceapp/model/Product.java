package com.simplilearn.project.app.sportyshoesecommerceapp.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.TemporalType.TIMESTAMP;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product")
public class Product implements Serializable {
    private static final long serialVersionUID = 8967534090411260130L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    private int number;

    private String color;

    private String size;

    private String price;

    private String brand;

    private int stockLevel;

    @Lob
    private String description;

    @Temporal(TIMESTAMP)
    @CreationTimestamp
    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    @Temporal(TIMESTAMP)
    @UpdateTimestamp
    private Date updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", referencedColumnName = "id")
    @ToString.Exclude
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartItemId", referencedColumnName = "id")
    @ToString.Exclude
    private CartItem cartItem;


    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY,orphanRemoval = true)
    @ToString.Exclude
    private List<Image> images = new ArrayList<>();

    public String getFormatedPrice(){
        double amount = Double.parseDouble(price);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return "TSZ " + formatter.format(amount);
    }
    public String getFirstImagePath(){
        Image image = this.getImages().stream().findFirst().orElse(null);
        return image != null ? image.getPath() : "/images/no-image.png";
    }

}