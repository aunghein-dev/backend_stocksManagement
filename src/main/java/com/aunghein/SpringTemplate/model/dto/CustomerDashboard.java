package com.aunghein.SpringTemplate.model.dto;

import java.math.BigDecimal;

public interface CustomerDashboard {
     Long getRetailerCnt();
     Long getWholesalerCnt();
     Long getTotalCustomers();
     BigDecimal getTotalDue();
}
