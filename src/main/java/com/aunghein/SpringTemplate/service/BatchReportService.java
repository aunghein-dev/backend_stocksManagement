package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.dto.BatchReport;
import com.aunghein.SpringTemplate.repository.CheckoutRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchReportService {

    @Autowired
    private CheckoutRepo checkoutRepo;

    public List<BatchReport> getBatchHist(Long bizId) {
        return checkoutRepo.findBatchReportByBizId(bizId);
    }
}
