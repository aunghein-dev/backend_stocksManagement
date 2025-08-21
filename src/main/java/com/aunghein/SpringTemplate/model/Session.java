//package com.aunghein.SpringTemplate.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.Instant;
//
//@Entity
//@Data // Generates getters, setters, toString, equals, and hashCode
//@NoArgsConstructor // Generates a no-argument constructor
//@AllArgsConstructor
//@Builder
//public class Session {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long sessionId;
//
//    @Column(nullable = false)
//    private Long userId;
//    private Instant loginAt;
//    private Instant logoutAt;
//    private String deviceName;
//    private String geoRegion;
//    public enum Status { ACTIVE, INACTIVE }
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Status status;
//
//    public enum DeviceType { DESKTOP, MOBILE, TABLET, BOT, UNKNOWN }
//    @Enumerated(EnumType.STRING)
//    private DeviceType deviceType;
//
//    private Instant updatedAt;
//}
