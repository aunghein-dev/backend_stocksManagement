package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.Business;
import com.aunghein.SpringTemplate.repository.BusinessRepo;
import com.aunghein.SpringTemplate.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BusinessService {

    @Autowired
    private BusinessRepo businessRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SupabaseService supabaseService;

    public Business getBizInfoByEmail(String email) {
      return businessRepo.findBizByEmail(email);
    }

    @Transactional
    public ResponseEntity<?> editBusinessInfo(Long bizId, Business updatedBusinessInfo) {
        Business toEditBusiness = businessRepo.getReferenceById(bizId);
        toEditBusiness.setBusinessName(updatedBusinessInfo.getBusinessName());
        toEditBusiness.setAutoPrintAfterCheckout(updatedBusinessInfo.getAutoPrintAfterCheckout());
        toEditBusiness.setBusinessNameShortForm(updatedBusinessInfo.getBusinessNameShortForm());
        toEditBusiness.setDefaultCurrency(updatedBusinessInfo.getDefaultCurrency());
        toEditBusiness.setTaxRate(updatedBusinessInfo.getTaxRate());
        toEditBusiness.setInvoiceFooterMessage(updatedBusinessInfo.getInvoiceFooterMessage());
        toEditBusiness.setShowLogoOnInvoice(updatedBusinessInfo.getShowLogoOnInvoice());

        return ResponseEntity.ok(businessRepo.save(toEditBusiness));
    }

    public ResponseEntity<String> editBusinessLogoOnly(Long bizId, MultipartFile logo) {
        Business toEditBusiness = businessRepo.getReferenceById(bizId);
        String oldImgUrl = toEditBusiness.getBusinessLogo();

        if (oldImgUrl != null && oldImgUrl.startsWith("https://svmeynesalueoxzhtdqp.supabase.co")) {
            supabaseService.deleteFile(oldImgUrl);
        }

        String newUrl;
        try {
            newUrl = supabaseService.uploadBusinessLogo(logo);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload logo", e);
        }

        toEditBusiness.setBusinessLogo(newUrl);
        businessRepo.save(toEditBusiness);
        return ResponseEntity.ok(newUrl);
    }

}
