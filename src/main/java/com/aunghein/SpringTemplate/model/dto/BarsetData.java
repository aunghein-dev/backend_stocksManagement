package com.aunghein.SpringTemplate.model.dto;

import java.math.BigDecimal;
import java.util.List;

public interface BarsetData {
    String getGroupName();
    List<BigDecimal> getValue();
}
