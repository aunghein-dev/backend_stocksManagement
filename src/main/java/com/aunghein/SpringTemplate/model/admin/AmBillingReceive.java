package com.aunghein.SpringTemplate.model.admin;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "am_billing_receive")
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class AmBillingReceive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rowId;

    private String amBillingReceivingProvider1;
    private String amBillingReceivingProvider2;
    private String amBillingReceivingProvider3;

    private String amBillingProviderNumber;

}
