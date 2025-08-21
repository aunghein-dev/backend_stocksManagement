package com.aunghein.SpringTemplate.controller;

import com.aunghein.SpringTemplate.service.TellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teller")
@RequiredArgsConstructor
public class TellerController {

    private final TellerService tellerService;

    @GetMapping("/{bizId}")
    public ResponseEntity<?> getAllTellersForBiz(@PathVariable Long bizId){
        return ResponseEntity.ok(tellerService.getAllTellersForBiz(bizId));
    }
}
