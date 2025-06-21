package com.aunghein.SpringTemplate.model;

import jakarta.persistence.*;

@Entity
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

    public Users() {}

    public Users(String username, String password, String role, String fullName, String userImgUrl, String referredCode, Business business) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.userImgUrl = userImgUrl;
        this.referredCode = referredCode;
        this.business = business;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getReferredCode() {
        return referredCode;
    }

    public void setReferredCode(String referredCode) {
        this.referredCode = referredCode;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", fullName='" + fullName + '\'' +
                ", userImgUrl='" + userImgUrl + '\'' +
                ", referredCode='" + referredCode + '\'' +
                ", business=" + (business != null ? business.getBusinessName() : "null") +
                '}';
    }
}
