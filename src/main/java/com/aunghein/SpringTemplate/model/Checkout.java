package com.aunghein.SpringTemplate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "checkout")
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
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
    private BigDecimal itemUnitPrice;
    private BigDecimal subCheckout;
    private String tranUserEmail;
    private Long bizId;
    private String barcodeNo;

}
