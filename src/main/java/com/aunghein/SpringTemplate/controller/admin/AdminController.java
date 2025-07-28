package com.aunghein.SpringTemplate.controller.admin;

import com.aunghein.SpringTemplate.model.admin.AmBillingReceive;
import com.aunghein.SpringTemplate.model.billing.Invoice;
import com.aunghein.SpringTemplate.service.admin.AdminService;
import com.aunghein.SpringTemplate.service.admin.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final InvoiceService invoiceService;

    @GetMapping("/billing-receive")
    public ResponseEntity<?> getBillingReceiveInfoOfAdmin() {
        AmBillingReceive info = adminService.getBillingReceiveInfoOfAdmin();
        if (info == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(info);
    }

    @GetMapping("/invoices/{bizId}")
    public ResponseEntity<?> getAllInvoicesByBizId(@PathVariable Long bizId){
        List<Invoice> allInvoices = invoiceService.getAllInvoicesByBizId(bizId);
        if (allInvoices.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allInvoices);
    }

    @GetMapping("/specific/invoice/{tranId}")
    public ResponseEntity<?> getSpecificInvoiceByTranId(@PathVariable String tranId){
        Invoice specificInvoice = invoiceService.getSpecificInvoiceByTranId(tranId);
        if (specificInvoice == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specificInvoice);
    }
}
