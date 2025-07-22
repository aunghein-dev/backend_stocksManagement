package com.aunghein.SpringTemplate.controller;

import com.aunghein.SpringTemplate.model.Customer;
import com.aunghein.SpringTemplate.model.StkGroup;
import com.aunghein.SpringTemplate.model.dto.CustomerDashboard;
import com.aunghein.SpringTemplate.repository.CustomerRepo;
import com.aunghein.SpringTemplate.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {


    @Autowired
    private CustomerService cusService;

    @GetMapping("/dash/biz/{bizId}")
    public ResponseEntity<CustomerDashboard> getCustomerDashboard(@PathVariable Long bizId){
        return ResponseEntity.ok(cusService.getCustomerDashboard(bizId));
    }

    @GetMapping("/biz/{bizId}")
    public ResponseEntity<List<Customer>> getAllCustomer(@PathVariable Long bizId) {
        List<Customer> allCustomers = cusService.findAllByBizId(bizId);
        return ResponseEntity.ok(allCustomers);
    }

    @PostMapping(value = "/new/biz/{bizId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Customer> createNewCustomer(@PathVariable Long bizId,
                                                      @RequestPart("customer") Customer newCustomer,
                                                      @RequestPart(value = "image", required = false) MultipartFile customerFileImg) {
        return ResponseEntity.ok(cusService.createNewCustomer(bizId, newCustomer, customerFileImg));
    }

    @PutMapping(value = "/update/biz/{bizId}")
    public ResponseEntity<Customer> updateExistingCustomer(@PathVariable Long bizId,
                                                         @RequestPart("customer") Customer replacedCustomer,
                                                         @RequestPart(value = "image", required = false) MultipartFile customerFileImg) {

        return ResponseEntity.ok(cusService.updateExistingCustomer(bizId,replacedCustomer,customerFileImg));
    }

    @DeleteMapping("/delete/biz/{bizId}/cid/{cid}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable Long bizId, @PathVariable String cid){
        return ResponseEntity.ok(cusService.deleteCustomer(bizId, cid));
    }
}
