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
@Table(name = "category")
public class Category implements Serializable {
    private static final long serialVersionUID = 5552230012462390888L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    private String name;

    private String image;

    private String description;

    private long count;

    @Temporal(TIMESTAMP)
    @CreationTimestamp
    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    @Temporal(TIMESTAMP)
    @UpdateTimestamp
    private Date updatedDate;

    @OneToMany(mappedBy="category",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    List<Product> productList = new ArrayList<>();
}