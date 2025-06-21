package com.aunghein.SpringTemplate.controller;

import com.aunghein.SpringTemplate.controller.Utils.GetTokenFromRequest;
import com.aunghein.SpringTemplate.model.dto.Account;
import com.aunghein.SpringTemplate.model.Business;
import com.aunghein.SpringTemplate.model.Users;
import com.aunghein.SpringTemplate.repository.UserRepo;
import com.aunghein.SpringTemplate.service.BusinessService;
import com.aunghein.SpringTemplate.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/info")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepo userRepo;


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
                                            HttpServletRequest request,
                                            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = null;

        // 1. Try from cookie
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 2. Try from Authorization header
        if (token == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty token");
        }

        String username = jwtService.extractUsername(token);

        Business business = businessService.getBizInfoByEmail(username);

        return ResponseEntity.ok(business);
    }


    @GetMapping("/me/account")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = GetTokenFromRequest.getToken(request, authHeader);

        String username = jwtService.extractUsername(token);
        Users user = userRepo.findByUsername(username);
        Account userAccount = new Account() {
            @Override
            public Long getAccountId() {
                return user.getId();
            }

            @Override
            public String getUsername() {
                return user.getUsername();
            }

            @Override
            public String getRole() {
                return user.getRole();
            }

            @Override
            public String getFullName() {
                return user.getFullName();
            }

            @Override
            public String getUserImgUrl() {
                return user.getUserImgUrl();
            }

            @Override
            public Business getBusiness() {
                return user.getBusiness();
            }
        };
        System.out.println("USER ACCOUNT CHECKING :************* " + userAccount.toStringSafe());
        return ResponseEntity.ok(userAccount);
    }

    @PutMapping("/edit/{bizId}")
    public ResponseEntity<?> editBusinessInfo(@PathVariable Long bizId,
                                              @RequestBody Business updatedBusinessInfo){
        return businessService.editBusinessInfo(bizId, updatedBusinessInfo);
    }


    @PutMapping("/upload/logo/{bizId}")
    public ResponseEntity<String> editBusinessLogoOnly(@PathVariable Long bizId,
                                                  @RequestPart("logo") MultipartFile logo){
        return businessService.editBusinessLogoOnly(bizId, logo);
    }


}
