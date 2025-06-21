package com.aunghein.SpringTemplate.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "business")
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
    // Based on your frontend, it's a message, not a true/false flag.
    private String invoiceFooterMessage; // Changed to String

    private String streets;
    private String township;
    private String city;
    private String phoneNum1;
    private String phoneNum2;

    public Business() {}

    public Business(Long businessId, String businessLogo, String businessName, String businessNameShortForm, String registeredBy, Date registeredAt, String defaultCurrency, Double taxRate, Boolean showLogoOnInvoice, Boolean autoPrintAfterCheckout, String invoiceFooterMessage, String streets, String township, String city, String phoneNum1, String phoneNum2) {
        this.businessId = businessId;
        this.businessLogo = businessLogo;
        this.businessName = businessName;
        this.businessNameShortForm = businessNameShortForm;
        this.registeredBy = registeredBy;
        this.registeredAt = registeredAt;
        this.defaultCurrency = defaultCurrency;
        this.taxRate = taxRate;
        this.showLogoOnInvoice = showLogoOnInvoice;
        this.autoPrintAfterCheckout = autoPrintAfterCheckout;
        this.invoiceFooterMessage = invoiceFooterMessage;
        this.streets = streets;
        this.township = township;
        this.city = city;
        this.phoneNum1 = phoneNum1;
        this.phoneNum2 = phoneNum2;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessLogo() {
        return businessLogo;
    }

    public void setBusinessLogo(String businessLogo) {
        this.businessLogo = businessLogo;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessNameShortForm() {
        return businessNameShortForm;
    }

    public void setBusinessNameShortForm(String businessNameShortForm) {
        this.businessNameShortForm = businessNameShortForm;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public Boolean getShowLogoOnInvoice() {
        return showLogoOnInvoice;
    }

    public void setShowLogoOnInvoice(Boolean showLogoOnInvoice) {
        this.showLogoOnInvoice = showLogoOnInvoice;
    }

    public Boolean getAutoPrintAfterCheckout() {
        return autoPrintAfterCheckout;
    }

    public void setAutoPrintAfterCheckout(Boolean autoPrintAfterCheckout) {
        this.autoPrintAfterCheckout = autoPrintAfterCheckout;
    }

    public String getInvoiceFooterMessage() {
        return invoiceFooterMessage;
    }

    public void setInvoiceFooterMessage(String invoiceFooterMessage) {
        this.invoiceFooterMessage = invoiceFooterMessage;
    }

    public String getStreets() {
        return streets;
    }

    public void setStreets(String streets) {
        this.streets = streets;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNum1() {
        return phoneNum1;
    }

    public void setPhoneNum1(String phoneNum1) {
        this.phoneNum1 = phoneNum1;
    }

    public String getPhoneNum2() {
        return phoneNum2;
    }

    public void setPhoneNum2(String phoneNum2) {
        this.phoneNum2 = phoneNum2;
    }

    @Override
    public String toString() {
        return "Business{" +
                "businessId=" + businessId +
                ", businessLogo='" + businessLogo + '\'' +
                ", businessName='" + businessName + '\'' +
                ", businessNameShortForm='" + businessNameShortForm + '\'' +
                ", registeredBy='" + registeredBy + '\'' +
                ", registeredAt=" + registeredAt +
                ", defaultCurrency='" + defaultCurrency + '\'' +
                ", taxRate=" + taxRate +
                ", showLogoOnInvoice=" + showLogoOnInvoice +
                ", autoPrintAfterCheckout=" + autoPrintAfterCheckout +
                ", invoiceFooterMessage='" + invoiceFooterMessage + '\'' +
                ", streets='" + streets + '\'' +
                ", township='" + township + '\'' +
                ", city='" + city + '\'' +
                ", phoneNum1='" + phoneNum1 + '\'' +
                ", phoneNum2='" + phoneNum2 + '\'' +
                '}';
    }
}