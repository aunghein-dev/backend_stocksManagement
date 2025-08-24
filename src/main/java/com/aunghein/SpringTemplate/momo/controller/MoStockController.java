package com.aunghein.SpringTemplate.momo.controller;

import com.aunghein.SpringTemplate.service.StkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/mo/v1/stock")
@RequiredArgsConstructor
public class MoStockController {

    private final StkService stkService;

    @GetMapping
    public ResponseEntity<?> getStocks(){
        return ResponseEntity.ok(stkService.getStkGroupByBusinessId(1L));
    }

}
