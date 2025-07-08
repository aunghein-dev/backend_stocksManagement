package com.aunghein.SpringTemplate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;


@Entity
@Table(name = "stk_item")
public class StkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private String itemImage;

    private String itemColorHex;
    private int itemQuantity;
    private String barcodeNo;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonBackReference
    private StkGroup stkGroup;

    public StkItem() {
    }

    public StkItem(Long itemId, String itemImage, String itemColorHex, int itemQuantity, String barcodeNo, StkGroup stkGroup) {
        this.itemId = itemId;
        this.itemImage = itemImage;
        this.itemColorHex = itemColorHex;
        this.itemQuantity = itemQuantity;
        this.barcodeNo = barcodeNo;
        this.stkGroup = stkGroup;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemColorHex() {
        return itemColorHex;
    }

    public void setItemColorHex(String itemColorHex) {
        this.itemColorHex = itemColorHex;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public StkGroup getStkGroup() {
        return stkGroup;
    }

    public void setStkGroup(StkGroup stkGroup) {
        this.stkGroup = stkGroup;
    }

    public String getBarcodeNo() {
        return barcodeNo;
    }

    public void setBarcodeNo(String barcodeNo) {
        this.barcodeNo = barcodeNo;
    }


    @Override
    public String toString() {
        return "StkItem{" +
                "itemId=" + itemId +
                ", itemImage='" + itemImage + '\'' +
                ", itemColorHex='" + itemColorHex + '\'' +
                ", itemQuantity=" + itemQuantity +
                ", barcodeNo='" + barcodeNo + '\'' +
                ", stkGroup=" + stkGroup +
                '}';
    }
}
