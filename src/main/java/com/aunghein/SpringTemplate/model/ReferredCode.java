package com.aunghein.SpringTemplate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class ReferredCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private Boolean isUsed;
    private Date createdAt;
    private String usedBy;

    public ReferredCode() {
    }

    public ReferredCode(Long id, String code, Boolean isUsed, Date createdAt, String usedBy) {
        this.id = id;
        this.code = code;
        this.isUsed = isUsed;
        this.createdAt = createdAt;
        this.usedBy = usedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    @Override
    public String toString() {
        return "ReferredCode{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", isUsed=" + isUsed +
                ", createdAt=" + createdAt +
                ", usedBy='" + usedBy + '\'' +
                '}';
    }
}
