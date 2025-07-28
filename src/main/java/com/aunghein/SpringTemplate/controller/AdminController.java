package com.aunghein.SpringTemplate.controller;

import com.aunghein.SpringTemplate.model.admin.AmBillingReceive;
import com.aunghein.SpringTemplate.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @GetMapping("/billingReceiveInfo")
    public ResponseEntity<AmBillingReceive> getBillingReceiveInfoOfAdmin(){
        return ResponseEntity.ok(adminService.getBillingReceiveInfoOfAdmin());
    }
}
