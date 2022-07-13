package com.simplilearn.project.app.sportyshoesecommerceapp.model;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Category;
import com.simplilearn.project.app.sportyshoesecommerceapp.model.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "contact")
public class Contact implements Serializable {
    private static final long serialVersionUID = 1298110191965226340L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

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

    @OneToOne(mappedBy = "contact")
    private User user;

}