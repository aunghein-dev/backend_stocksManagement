package com.aunghein.SpringTemplate.service.admin;

import com.aunghein.SpringTemplate.model.admin.AmBillingReceive;
import com.aunghein.SpringTemplate.repository.admin.AmBillingReceiveRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AmBillingReceiveRepo amBillingReceiveRepo;

    @Autowired
    public AdminService(AmBillingReceiveRepo amBillingReceiveRepo){
        this.amBillingReceiveRepo = amBillingReceiveRepo;
    }

    public AmBillingReceive getBillingReceiveInfoOfAdmin() {
        return amBillingReceiveRepo.findAll().getFirst();
    }
}
