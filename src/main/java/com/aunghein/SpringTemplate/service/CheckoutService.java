package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.*;
import com.aunghein.SpringTemplate.model.dto.CheckoutResponse;
import com.aunghein.SpringTemplate.model.dto.VouncherGen;
import com.aunghein.SpringTemplate.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CheckoutService {

    @Autowired
    private CheckoutRepo checkoutRepo;

    @Autowired
    private StkRepo stkRepo;

    @Autowired
    private BusinessRepo businessRepo;

    @Autowired
    private StkItemRepo stkItemRepo;

    @Autowired
    private PaymentRelateRepo paymentRelateRepo;

    @Autowired
    private CustomerRepo customerRepo;


    @Transactional
    public String checkout(Long bizId, List<Checkout> checkoutList, PaymentRelate paymentRelate) {

        // version 0.2
        if (paymentRelate != null && paymentRelate.getRelateCid() != null && !paymentRelate.getRelateCid().isEmpty()){
            Customer customer = customerRepo.findByCidAndBizId(paymentRelate.getRelateCid(),bizId).orElseThrow();
            if(paymentRelate.getRelatePaymentType().equals("wallet")){
                BigDecimal totalDueAmt = customer.getCustomerDueAmt().add(paymentRelate.getRelateFinalIncome());
                customer.setCustomerLastDueDate(new Date());
                customer.setCustomerDueAmt(totalDueAmt);
            }
            BigDecimal totalBought = customer.getBoughtAmt().add(paymentRelate.getRelateFinalIncome());
            Long cnt = customer.getBoughtCnt() + 1;
            customer.setLastShopDate(new Date());
            customer.setBoughtCnt(cnt);
            customer.setBoughtAmt(totalBought);
            customerRepo.save(customer);
            paymentRelateRepo.save(paymentRelate);
        }

        String batchId = checkoutList.getFirst().getBatchId();

        // Step 1: Fetch stock groups
        List<StkGroup> stkGroups = stkRepo.findStkGroupByBusinessId(bizId);
        Map<Long, StkGroup> groupMap = stkGroups.stream()
                .collect(Collectors.toMap(StkGroup::getGroupId, g -> g));

        Set<StkGroup> updatedGroups = new HashSet<>();

        for (Checkout co : checkoutList) {
            StkGroup group = groupMap.get(co.getStkGroupId());
            if (group != null) {
                for (StkItem item : group.getItems()) {
                    if (item.getItemId().equals(co.getStkItemId())) {
                        int newQty = item.getItemQuantity() - co.getCheckoutQty();
                        if (newQty < 0) {
                            throw new RuntimeException("Insufficient stock for item ID: " + item.getItemId());
                        }
                        item.setItemQuantity(newQty);
                        updatedGroups.add(group);
                        break;
                    }
                }
            }

            co.setBatchId(batchId);
            co.setBizId(bizId);
            if (co.getTranDate() == null) {
                co.setTranDate(new Date());
            }

            checkoutRepo.save(co);
        }

        stkRepo.saveAll(updatedGroups);

        return "Checkout completed for " + checkoutList.size() + " items.";
    }

    public void setBarcode(){

    }


    public List<CheckoutResponse> getCheckOut(Long bizId) {
        return checkoutRepo.findCheckoutByBizId(bizId);
    }

    @Transactional
    public Checkout refundCheckout(Long bizId, Long tranId, Integer newQty) {
        System.out.printf("Initiating refund: bizId=%d, tranId=%d, newQty=%d%n", bizId, tranId, newQty);

        // Retrieve the checkout record based on business ID and transaction ID
        Checkout selectedCheckout = checkoutRepo.findCheckoutByBizIdTranId(bizId, tranId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Checkout not found for bizId=%d and tranId=%d", bizId, tranId)));

        // Retrieve the associated stock item
        StkItem item = stkItemRepo.findItemByCustomId(selectedCheckout.getStkItemId());

        if (item == null) {
            throw new EntityNotFoundException(
                    String.format("Stock item not found or deleted for itemId=%s", selectedCheckout.getStkItemId()));
        }

        // Calculate the quantity difference to be refunded
        final int originalQty = selectedCheckout.getCheckoutQty();
        final int restoredQty = originalQty - newQty;

        if (restoredQty < 0) {
            throw new IllegalArgumentException("New quantity cannot exceed original checkout quantity.");
        }

        final int updatedItemQty = item.getItemQuantity() + restoredQty;

        // Apply updates
        item.setItemQuantity(updatedItemQty);
        selectedCheckout.setCheckoutQty(newQty);
        selectedCheckout.setTranDate(new Date());

        // Save changes
        stkItemRepo.save(item);
        Checkout updatedCheckout = checkoutRepo.save(selectedCheckout);

        System.out.printf("Refund processed successfully: itemId=%s, updatedQty=%d%n",
                item.getItemId(), updatedItemQty);

        return updatedCheckout;
    }


    @Transactional
    public Checkout cancelCheckout(Long bizId, Long tranId) {
        System.out.printf("Initiating checkout cancellation: bizId=%d, tranId=%d%n", bizId, tranId);

        Checkout checkoutToCancel = checkoutRepo.findCheckoutByBizIdTranId(bizId, tranId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Checkout not found for bizId=%d and tranId=%d", bizId, tranId)));

        StkItem item = stkItemRepo.findItemByCustomId(checkoutToCancel.getStkItemId());

        if (item == null) {
            checkoutRepo.delete(checkoutToCancel);
            System.out.printf("Checkout deleted but stock item was not found: itemId=%s%n", checkoutToCancel.getStkItemId());
            return checkoutToCancel;
        }

        int restoreQty = checkoutToCancel.getCheckoutQty();
        int updatedItemQty = item.getItemQuantity() + restoreQty;

        item.setItemQuantity(updatedItemQty);
        stkItemRepo.save(item);
        checkoutRepo.delete(checkoutToCancel);

        System.out.printf("Checkout canceled: itemId=%s, restoredQty=%d, newItemQty=%d%n",
                item.getItemId(), restoreQty, updatedItemQty);

        return checkoutToCancel;
    }



    public List<VouncherGen> getVoucherData(String batchId, Long bizId) {
        return checkoutRepo.getVoucherData(batchId, bizId);
    }
}
