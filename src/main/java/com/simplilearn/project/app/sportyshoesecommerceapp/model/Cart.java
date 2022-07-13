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
@Table(name = "cart")
public class Cart implements Serializable {
    private static final long serialVersionUID = 4125011150912178771L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String sessionId;

    private boolean status;

    private String fullname;

    private String mobile;

    private String email;

    private String address1;

    private String address2;

    private String city;

    private String country;

    @Temporal(TIMESTAMP)
    @CreationTimestamp
    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    @Temporal(TIMESTAMP)
    @UpdateTimestamp
    private Date updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy="cart",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    List<CartItem> cartItemList = new ArrayList<>();


}