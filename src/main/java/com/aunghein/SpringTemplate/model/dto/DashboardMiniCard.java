package com.aunghein.SpringTemplate.model.dto;

import java.math.BigDecimal;

public interface DashboardMiniCard {
    BigDecimal getRevenue();
    double getGrowth();
    Long getOrders();
    Long getProducts();
    BigDecimal getProfit();
}
