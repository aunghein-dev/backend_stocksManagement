package com.aunghein.SpringTemplate.controller;

import com.aunghein.SpringTemplate.model.Checkout;
import com.aunghein.SpringTemplate.model.Customer;
import com.aunghein.SpringTemplate.model.PaymentRelate;
import com.aunghein.SpringTemplate.model.dto.VouncherGen;
import com.aunghein.SpringTemplate.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @GetMapping(value = "/{bizId}")
    public ResponseEntity<?> getCheckOut(@PathVariable Long bizId){
        return ResponseEntity.ok(checkoutService.getCheckOut(bizId));
    }

    @PostMapping(value = "/{bizId}")
    public ResponseEntity<String> checkout(@PathVariable Long bizId,
                                           @RequestPart(value = "tempJson") List<Checkout> checkout,
                                           @RequestPart(value = "paymentRelate") PaymentRelate paymentRelate) {
        String result = checkoutService.checkout(bizId, checkout, paymentRelate);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/refund/{bizId}/{tranId}/{newQty}")
    public ResponseEntity<Checkout> refundCheckout(@PathVariable Long bizId,
                                                   @PathVariable Long tranId,
                                                   @PathVariable Integer newQty){
        Checkout checkout = checkoutService.refundCheckout(bizId, tranId, newQty);
        return ResponseEntity.ok(checkout);
    }

    @DeleteMapping("/cancel/{bizId}/{tranId}")
    public ResponseEntity<Checkout> cancelCheckout(@PathVariable Long bizId,
                                                   @PathVariable Long tranId) {
        Checkout canceledCheckout = checkoutService.cancelCheckout(bizId, tranId);
        return ResponseEntity.ok(canceledCheckout);
    }

    @GetMapping("/generate/{batchId}/{bizId}")
    public ResponseEntity<List<VouncherGen>> getVoucherData(@PathVariable String batchId,
                                                            @PathVariable Long bizId){
        return ResponseEntity.ok(checkoutService.getVoucherData(batchId, bizId));
    }
}