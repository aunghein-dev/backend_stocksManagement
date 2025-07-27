package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.billing.CurrBilling;
import com.aunghein.SpringTemplate.repository.BillingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    private BillingRepo billingRepo;

    //Constructor Injection
    @Autowired
    public BillingService(BillingRepo billingRepo){
        this.billingRepo = billingRepo;
    }

    public CurrBilling getBillingOfCurrentPlan(Long bizId) {
        return billingRepo.getBillingOfCurrentPlanByBizId(bizId);
    }
}
