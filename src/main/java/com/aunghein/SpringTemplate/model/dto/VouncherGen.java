package com.aunghein.SpringTemplate.model.dto;

import java.math.BigDecimal;
import java.util.Date;

public interface VouncherGen {
    String getBatch();
    Date getDate();
    Long getId();
    String getName();
    BigDecimal getPrice();
    Long getQuantity();
    BigDecimal getSubTotal();
}
