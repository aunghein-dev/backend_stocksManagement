package com.aunghein.SpringTemplate.controller;

import com.aunghein.SpringTemplate.model.billing.CurrBilling;
import com.aunghein.SpringTemplate.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/biz/{bizId}")
    public ResponseEntity<CurrBilling> getBillingOfCurrentPlan(@PathVariable Long bizId){
        return ResponseEntity.ok(billingService.getBillingOfCurrentPlan(bizId));
    }

    @GetMapping("/storage/{bizId}")
    public ResponseEntity<?> getStorageReponse(@PathVariable Long bizId){
        return ResponseEntity.ok(billingService.getStorageResponse(bizId));
    }
}
