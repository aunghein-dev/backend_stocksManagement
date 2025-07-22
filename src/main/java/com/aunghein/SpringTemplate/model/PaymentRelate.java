package com.aunghein.SpringTemplate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Entity
@Table(name = "payment_relate")
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class PaymentRelate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rowId;

    //New Update v0.2
    private Long relateBizId;
    private String relateBatchId;
    private String relateCid;
    private BigDecimal relateDiscountAmt;
    private String relatePaymentType;
    private BigDecimal relateChange;
    private BigDecimal relateFinalIncome;
}
