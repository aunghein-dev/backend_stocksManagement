package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.Business;
import com.aunghein.SpringTemplate.repository.BusinessRepo;
import com.aunghein.SpringTemplate.repository.UserRepo;
import com.aunghein.SpringTemplate.service.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepo businessRepo;
    private final UserRepo userRepo;
    private final SupabaseService supabaseService;
    //private final MinioService minioService;

    public Business getBizInfoByEmail(String email) {
      return businessRepo.findBusinessByUsername(email);
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


        /*
        //DELETE
        if (oldImgUrl != null && oldImgUrl.contains("file.openwaremyanmar")){
            try {
                minioService.deleteFile(oldImgUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete file of minio",e);
            }
        }

        //UPLOAD
        String minioNewUrl = "";
        try {
            minioNewUrl = minioService.uploadFile(logo);
        } catch (Exception e){
            throw new RuntimeException("Failed to upload logo to minio", e);
        }
        */

        toEditBusiness.setBusinessLogo(newUrl);
        businessRepo.save(toEditBusiness);
        return ResponseEntity.ok(newUrl);
    }

}
