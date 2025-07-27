package com.aunghein.SpringTemplate.controller;

import com.aunghein.SpringTemplate.model.billing.CurrBilling;
import com.aunghein.SpringTemplate.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/billing")
public class BillingController {


    @Autowired
    private BillingService billingService;

    @GetMapping("/biz/{bizId}")
    public ResponseEntity<CurrBilling> getBillingOfCurrentPlan(@PathVariable Long bizId){
        return ResponseEntity.ok(billingService.getBillingOfCurrentPlan(bizId));
    }
}
