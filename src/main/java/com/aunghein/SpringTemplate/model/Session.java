package com.aunghein.SpringTemplate.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "session",
        uniqueConstraints = @UniqueConstraint(name = "uk_session_user", columnNames = "userId")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @Column(nullable = false, unique = true) // reinforce at JPA level
    private Long userId;

    @Column(nullable = false)
    private Instant loginAt;

    private Instant logoutAt;

    private String ipAddress;
    private String userAgent;

    private String deviceName;
    private String geoRegion;
    private String geoCity;
    private Double latitude;
    private Double longitude;

    public enum Status { ACTIVE, INACTIVE }
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum DeviceType { DESKTOP, MOBILE, TABLET, BOT, UNKNOWN }
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    private Instant updatedAt;
}
