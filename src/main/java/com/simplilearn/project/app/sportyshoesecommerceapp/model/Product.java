package com.simplilearn.project.app.sportyshoesecommerceapp.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.TemporalType.TIMESTAMP;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
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

    private String price;

    private String manufacturer;

    private int stockLevel;

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
    private Category category;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

}