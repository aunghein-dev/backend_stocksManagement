package com.aunghein.SpringTemplate.model.billing;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "invoice")
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rowId;

    private String bizName;
    private String billingAcc;
    private Date billedDate;
    private Date issueDate;
    private Date expireDate;
    private Long issueDay;
    private int issueMonthCnt;
    private BigDecimal tranAmt;
    private String tranProvider;
    private String tranHistId;
    private String planCode;
    private String billedBy;
}
