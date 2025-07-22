package com.aunghein.SpringTemplate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    private Long id;

    private String username;
    private String password;
    private String role;
    private String fullName;
    private String userImgUrl;
    private String referredCode;

    @ManyToOne
    @JoinColumn(name = "business_id") // foreign key in users table
    private Business business;

}
