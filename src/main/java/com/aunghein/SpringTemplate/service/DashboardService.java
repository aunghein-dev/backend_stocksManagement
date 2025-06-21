package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.dto.*;
import com.aunghein.SpringTemplate.repository.CheckoutRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private CheckoutRepo checkoutRepo;

    public List<RadarData> getRadarMetricsData(Long bizId) {
        return checkoutRepo.getRadarDataByBizId(bizId);
    }

    public DashboardMiniCard getMiniCardData(Long bizId) {
        return checkoutRepo.getDashboardSummary(bizId);
    }

    public StorageLimitRateProjection getStorageUsage(Long bizId) {
        return checkoutRepo.getStorageUsage(bizId);
    }

    public List<PieData> getPieData(Long bizId) {
        return checkoutRepo.getPieData(bizId);
    }

    public List<LinechartData> getLineChartData(Long bizId) {
        return checkoutRepo.getLineChartData(bizId);
    }

    public List<BarsetDataDTO> getBarsetDataByMonth(Long bizId) {
        return checkoutRepo.getTopSoldByMonth(bizId);
    }
}
