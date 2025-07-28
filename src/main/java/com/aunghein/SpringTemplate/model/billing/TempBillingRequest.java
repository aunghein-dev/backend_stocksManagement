package com.aunghein.SpringTemplate.model.billing;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "temp_billing_request")
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class TempBillingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rowId;

    private String billingRequestId;
    private String billingAcc;
    private String reqPlanCode;
    private String reqPlanName;
    private Date reqEffectiveDate;
    private Date reqExpireDate;
    private String amBillingReceiveNumber;
    private BigDecimal amReceiveAmt;
    private BigDecimal issueAmt; //Original Cost Amount of Requested Plan
    private int issueDays; // Default in App always month (30) days
    private Boolean isConfirmed;
}
