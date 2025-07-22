package com.aunghein.SpringTemplate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "business")
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long businessId;

    private String businessLogo;
    private String businessName;
    private String businessNameShortForm;
    private String registeredBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;

    private String defaultCurrency;

    // Use wrapper type for nullable columns
    private Double taxRate; // Use Double (wrapper) for float/double if it can be null
    private Boolean showLogoOnInvoice;
    private Boolean autoPrintAfterCheckout;

    // IMPORTANT: 'invoiceFooterMessage' should almost certainly be a String, not a Boolean.
    private String invoiceFooterMessage; // Changed to String

    private String streets;
    private String township;
    private String city;
    private String phoneNum1;
    private String phoneNum2;

    // All the constructors, getters, setters, and the toString() method are now
}