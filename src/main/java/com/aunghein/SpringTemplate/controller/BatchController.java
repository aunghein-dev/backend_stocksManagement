package com.aunghein.SpringTemplate.controller;

import com.aunghein.SpringTemplate.model.dto.BatchReport;
import com.aunghein.SpringTemplate.service.BatchReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/batch")
public class BatchController {

    @Autowired
    private BatchReportService batchReportService;

    @GetMapping("/{bizId}")
    public ResponseEntity<List<BatchReport>> getBatchHist(@PathVariable Long bizId){
        return ResponseEntity.ok(batchReportService.getBatchHist(bizId));
    }
}
