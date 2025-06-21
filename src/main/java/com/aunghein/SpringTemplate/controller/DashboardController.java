package com.aunghein.SpringTemplate.controller;

import com.aunghein.SpringTemplate.model.dto.*;
import com.aunghein.SpringTemplate.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @RequestMapping("/minicard/{bizId}")
    public ResponseEntity<DashboardMiniCard> getMiniCardData(@PathVariable Long bizId){
        return ResponseEntity.ok(dashboardService.getMiniCardData(bizId));
    }

    @RequestMapping("/radar/{bizId}")
    public ResponseEntity<List<RadarData>> getRadarMetricsData(@PathVariable Long bizId){
        return ResponseEntity.ok(dashboardService.getRadarMetricsData(bizId));
    }

    @RequestMapping("/storage/{bizId}")
    public ResponseEntity<StorageLimitRateProjection> getStorageUsage(@PathVariable Long bizId){
        return ResponseEntity.ok(dashboardService.getStorageUsage(bizId));
    }

    @RequestMapping("/pie/{bizId}")
    public ResponseEntity<List<PieData>> getPieData(@PathVariable Long bizId){
        return ResponseEntity.ok(dashboardService.getPieData(bizId));
    }

    @RequestMapping("/linechart/{bizId}")
    public ResponseEntity<List<LinechartData>> getLinechartData(@PathVariable Long bizId){
        return ResponseEntity.ok(dashboardService.getLineChartData(bizId));
    }

    @RequestMapping("/barset/{bizId}")
    public ResponseEntity<List<BarsetDataDTO>> getBarsetDataByMonth(@PathVariable Long bizId){
        return ResponseEntity.ok(dashboardService.getBarsetDataByMonth(bizId));
    }
}
