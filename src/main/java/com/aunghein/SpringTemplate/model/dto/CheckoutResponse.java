package com.aunghein.SpringTemplate.model.dto;

import java.math.BigDecimal;
import java.util.Date;

public interface CheckoutResponse {
    Long getTranId();
    Date getTranDate();
    String getBatchId();
    Long getStkGroupId();
    String getStkGroupName();
    Long getStkItemId();
    int getCheckoutQty();
    BigDecimal getItemUnitPrice();
    BigDecimal getSubCheckout();
    String getTranUserEmail();
    Long getBizId();
    String getBarcodeNo();

    // extra computed fields
    BigDecimal getGroupOriginalPrice();
    BigDecimal getSubOriginal();
}
