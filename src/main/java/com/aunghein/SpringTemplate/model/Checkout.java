package com.aunghein.SpringTemplate.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tranID;

    @Temporal(TemporalType.TIMESTAMP)
    private Date tranDate;

    private String batchId;
    private Long stkGroupId;
    private String stkGroupName;
    private Long stkItemId;
    private int checkoutQty;
    private float itemUnitPrice;
    private float subCheckout;
    private String tranUserEmail;
    private Long bizId;

    public Checkout() {
        // Required by JPA
    }

    public Checkout(Date tranDate, String batchId, Long stkGroupId, String stkGroupName, Long stkItemId, int checkoutQty, float itemUnitPrice, float subCheckout, String tranUserEmail, Long bizId) {
        this.tranDate = tranDate;
        this.batchId = batchId;
        this.stkGroupId = stkGroupId;
        this.stkGroupName = stkGroupName;
        this.stkItemId = stkItemId;
        this.checkoutQty = checkoutQty;
        this.itemUnitPrice = itemUnitPrice;
        this.subCheckout = subCheckout;
        this.tranUserEmail = tranUserEmail;
        this.bizId = bizId;
    }

    public Checkout(Long tranID, Date tranDate, String batchId, Long stkGroupId, String stkGroupName, Long stkItemId, int checkoutQty, float itemUnitPrice, float subCheckout, String tranUserEmail, Long bizId) {
        this.tranID = tranID;
        this.tranDate = tranDate;
        this.batchId = batchId;
        this.stkGroupId = stkGroupId;
        this.stkGroupName = stkGroupName;
        this.stkItemId = stkItemId;
        this.checkoutQty = checkoutQty;
        this.itemUnitPrice = itemUnitPrice;
        this.subCheckout = subCheckout;
        this.tranUserEmail = tranUserEmail;
        this.bizId = bizId;
    }

    public Long getTranID() {
        return tranID;
    }

    public void setTranID(Long tranID) {
        this.tranID = tranID;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public Long getStkGroupId() {
        return stkGroupId;
    }

    public void setStkGroupId(Long stkGroupId) {
        this.stkGroupId = stkGroupId;
    }

    public String getStkGroupName() {
        return stkGroupName;
    }

    public void setStkGroupName(String stkGroupName) {
        this.stkGroupName = stkGroupName;
    }

    public Long getStkItemId() {
        return stkItemId;
    }

    public void setStkItemId(Long stkItemId) {
        this.stkItemId = stkItemId;
    }

    public int getCheckoutQty() {
        return checkoutQty;
    }

    public void setCheckoutQty(int checkoutQty) {
        this.checkoutQty = checkoutQty;
    }

    public float getItemUnitPrice() {
        return itemUnitPrice;
    }

    public void setItemUnitPrice(float itemUnitPrice) {
        this.itemUnitPrice = itemUnitPrice;
    }

    public float getSubCheckout() {
        return subCheckout;
    }

    public void setSubCheckout(float subCheckout) {
        this.subCheckout = subCheckout;
    }

    public String getTranUserEmail() {
        return tranUserEmail;
    }

    public void setTranUserEmail(String tranUserEmail) {
        this.tranUserEmail = tranUserEmail;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @Override
    public String toString() {
        return "Checkout{" +
                "tranID=" + tranID +
                ", tranDate=" + tranDate +
                ", batchId='" + batchId + '\'' +
                ", stkGroupId=" + stkGroupId +
                ", stkGroupName='" + stkGroupName + '\'' +
                ", stkItemId=" + stkItemId +
                ", checkoutQty=" + checkoutQty +
                ", itemUnitPrice=" + itemUnitPrice +
                ", subCheckout=" + subCheckout +
                ", tranUserEmail='" + tranUserEmail + '\'' +
                ", bizId=" + bizId +
                '}';
    }
}
