package com.aunghein.SpringTemplate.service.admin;

import com.aunghein.SpringTemplate.model.billing.Invoice;
import com.aunghein.SpringTemplate.repository.admin.InvoiceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepo invoiceRepo;

    public List<Invoice> getAllInvoicesByBizId(Long bizId) {
        return invoiceRepo.getAllInvoicesByBizId(bizId);
    }

    public Invoice getSpecificInvoiceByTranId(String tranId) {
        return invoiceRepo.getSpecificInvoiceByTranId(tranId);
    }
}
