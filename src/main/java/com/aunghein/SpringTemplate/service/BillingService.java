package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.billing.CurrBilling;
import com.aunghein.SpringTemplate.model.dto.StorageProjection;
import com.aunghein.SpringTemplate.model.dto.StorageResponse;
import com.aunghein.SpringTemplate.repository.BillingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillingRepo billingRepo;

    public CurrBilling getBillingOfCurrentPlan(Long bizId) {
        return billingRepo.getBillingOfCurrentPlanByBizId(bizId);
    }

    public StorageResponse getStorageResponse(Long bizId) {
        StorageProjection p = billingRepo.getStorageResponse(bizId);

        if (p == null || !StringUtils.hasText(p.getLongName())) {
            return StorageResponse.builder()
                    .limitStorageKb(512_000L)
                    .limitStorageTxt("500 MB")
                    .longName("Free Plan")
                    .build();
        }

        return StorageResponse.builder()
                .limitStorageKb(p.getLimitStorageKb())
                .limitStorageTxt(p.getLimitStorageTxt())
                .longName(p.getLongName())
                .build();
    }
}
