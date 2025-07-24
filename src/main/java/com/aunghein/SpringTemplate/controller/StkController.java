package com.aunghein.SpringTemplate.controller;

import com.aunghein.SpringTemplate.model.Business;
import com.aunghein.SpringTemplate.model.StkGroup;
import com.aunghein.SpringTemplate.repository.BusinessRepo;
import com.aunghein.SpringTemplate.service.StkService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.NoTransactionException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@RestController
public class StkController {

    @Autowired
    private StkService service;

    @Autowired
    private BusinessRepo businessRepo;

    @GetMapping("/health")
    public ResponseEntity<String> safeCronTask() {
    try {
        // ✅ Warm-up delay — give time for app to fully boot
        Thread.sleep(2000); // 2s is usually enough

        // ✅ Then run actual logic (fetch DB, call APIs, etc.)
        System.out.println("✅ Cron task started: " + LocalDateTime.now());

        return ResponseEntity.ok("✅ Task completed");
        } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Task failed");
       }
     }

    // Pagination
    @GetMapping("/stkG/biz/{bizId}/page")
    public Page<StkGroup> getStkGroupByBusinessIdPaged(
            @PathVariable Long bizId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getStkGroupsByBusinessId(bizId, pageable);
    }


    //By Group
    @GetMapping("/stkG/biz/{bizId}")
    public List<StkGroup> getStkGroupByBusinessId(@PathVariable Long bizId) {
        return service.getStkGroupByBusinessId(bizId);
    }

    //By Group Filter Non-Zero Qty
    @GetMapping("/stkG/biz/nonZero/{bizId}")
    public List<StkGroup> getStkGroupByBizNonZeroItems(@PathVariable Long bizId) {
        return service.getStkGroupByBizNonZeroItems(bizId);
    }

    @GetMapping("/biz/{bizId}/stkG/{groupId}")
    public Optional<StkGroup> getStkGroupByGroupId(@PathVariable Long groupId,
                                                   @PathVariable Long bizId){
        return service.getStkGroupByGroupId(groupId, bizId);
    }

    //By Business
    @GetMapping("/stkG/business/{bizId}")
    public Business getBusinessWithGroups(@PathVariable Long bizId) {
        return businessRepo.findById(bizId)
                .orElseThrow(() -> new RuntimeException("Business not found"));
    }


    @CrossOrigin
    @PostMapping(value = "/stkG/biz/{bizId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StkGroup> createStkGroup(@PathVariable Long bizId,
                                   @RequestPart("json") StkGroup stkGroup,
                                   @RequestPart("groupImage") MultipartFile groupImage,
                                   @RequestPart("itemImages") List<MultipartFile> itemImages) {
        return ResponseEntity.ok(service.createStkGroupByBusinessId(bizId, stkGroup, groupImage, itemImages));
    }

    @CrossOrigin
    @DeleteMapping("/stkG/{groupId}/biz/{bizId}")
    public ResponseEntity<String> deleteStkGroup(@PathVariable Long bizId, @PathVariable Long groupId) {
        try {
            service.deleteStkGroupByBusinessIdWithGroupId(bizId, groupId);
            return ResponseEntity.ok("Stock group deleted successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NoTransactionException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Optional for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
        }
    }



    @PutMapping(value = "/edit/stkG/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StkGroup> editStkGroup(@PathVariable Long groupId,
                                                 @RequestPart("json") StkGroup stkGroup,
                                                 @RequestPart(name = "groupImage", required = false) MultipartFile groupImage,
                                                 @RequestPart(name = "itemImages", required = false) List<MultipartFile> itemImages) {
        StkGroup updatedGroup = service.editByBusiness_BusinessIdWithGroupId(groupId, stkGroup,groupImage,itemImages);
        return ResponseEntity.ok(updatedGroup);
    }


    @DeleteMapping("/delete/stkItem/{groupId}/{itemId}")
    public ResponseEntity<?> deleteItemOnlyByItemId(@PathVariable Long itemId,
                                                    @PathVariable Long groupId){

        return ResponseEntity.ok(service.deleteItemOnlyByItemId(itemId, groupId));
    }

}
