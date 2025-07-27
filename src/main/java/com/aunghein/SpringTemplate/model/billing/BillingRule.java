package com.aunghein.SpringTemplate.model.billing;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "billing_rule")
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class BillingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rowId;

    private String code;
    private String longName;
    private double price;
    private double costPerDay;
    private BigDecimal limitStorageKb;
    private String limitStorageTxt;
    private Long limitAccountCnt;
}
