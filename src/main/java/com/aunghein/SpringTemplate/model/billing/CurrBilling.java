package com.aunghein.SpringTemplate.model.billing;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "curr_billing")
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class CurrBilling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rowId;

    private Long bizId;
    private String billingAcc;
    private Date currIssueDate;
    private Date currExpireDate;
    private String currPlanCode;
    private BigDecimal currIssueAmt;
    private Long currIssueDay;
}