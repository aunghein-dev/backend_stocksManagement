package com.aunghein.SpringTemplate.model.dto;

import java.math.BigDecimal;
import java.util.Date;

public interface BatchReport {
    String getBatchId();
    int getTotalQty();
    int getStkItemCnt();
    BigDecimal getCheckoutTotal();
    Date getTranDate();
    String getTranUserEmail();
    Long getBizId();
}

