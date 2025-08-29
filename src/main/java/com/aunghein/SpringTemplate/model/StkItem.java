package com.aunghein.SpringTemplate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "stk_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
