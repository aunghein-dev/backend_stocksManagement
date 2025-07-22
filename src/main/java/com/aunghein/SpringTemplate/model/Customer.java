package com.aunghein.SpringTemplate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "customer")
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rowId;
    private String cid;
    private String name;
    private String typeOfCustomer;
    private String address1;
    private String phoneNo1;
    private String phoneNo2;
    private String township;
    private String city;
    private Long boughtCnt;
    private BigDecimal boughtAmt;
    private BigDecimal customerDueAmt;
    private Date customerLastDueDate;
    private Date lastShopDate;
    private String imgUrl;
    private Long bizId;

    // You can remove all the explicit getters, setters, constructors, and toString method.
    // Lombok will generate them for you based on the annotations.
}