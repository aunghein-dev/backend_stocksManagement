package com.aunghein.SpringTemplate.model.dto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class BarsetDataDTO implements BarsetData {
    private final String groupName;
    private final List<BigDecimal> value;

    public BarsetDataDTO(String groupName,
                         Number jan, Number feb, Number mar, Number apr,
                         Number may, Number jun, Number jul, Number aug,
                         Number sep, Number oct, Number nov, Number dec) {
        this.groupName = groupName;
        this.value = Arrays.asList(
                toBigDecimal(jan),
                toBigDecimal(feb),
                toBigDecimal(mar),
                toBigDecimal(apr),
                toBigDecimal(may),
                toBigDecimal(jun),
                toBigDecimal(jul),
                toBigDecimal(aug),
                toBigDecimal(sep),
                toBigDecimal(oct),
                toBigDecimal(nov),
                toBigDecimal(dec)
        );
    }

    private static BigDecimal toBigDecimal(Number n) {
        if (n == null) return BigDecimal.ZERO;
        if (n instanceof BigDecimal) return (BigDecimal) n;
        return new BigDecimal(n.toString());
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    @Override
    public List<BigDecimal> getValue() {
        return value;
    }
}
