package com.aunghein.SpringTemplate.controller;

import com.aunghein.SpringTemplate.controller.Utils.GetTokenFromRequest;
import com.aunghein.SpringTemplate.model.Business;
import com.aunghein.SpringTemplate.model.Users;
import com.aunghein.SpringTemplate.model.dto.StorageResponse;
import com.aunghein.SpringTemplate.repository.UserRepo;
import com.aunghein.SpringTemplate.service.*;
import com.aunghein.SpringTemplate.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final JWTService jwtService;
    private final UserRepo userRepo;
    private final BusinessService businessService;
    private final BillingService billingService;
    private final SessionService sessionService;

    @PutMapping("/reset/password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable Long id,
                                           @RequestPart("newPassword") String newPassword) {
        return ResponseEntity.ok(service.resetPassword(id, newPassword));
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> newUserRegister(@RequestPart("postingUser") Users newUser,
                                             @RequestPart("postingBusiness") Business newBusiness){
        System.out.println("Received registration request for user: " + newUser.getUsername() + " and business: " + newBusiness.getBusinessName());
        System.out.println("Referred Code: " + newUser.getReferredCode());

         if(service.hasValueCode(newUser.getReferredCode())==0) {
            // This is the correct way to send a JSON error response
            Map<String, String> errorResponse = Collections.singletonMap("message", "Secret code is invalid.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND) // 404
                    .body(errorResponse); // This will be serialized to JSON: {"message": "..."}
         } else if(service.checkIsUsedSecretCode(newUser.getReferredCode())) {
            // This is the correct way to send a JSON error response
            Map<String, String> errorResponse = Collections.singletonMap("message", "Secret code has already been used. Please try again.");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409
                    .body(errorResponse); // This will be serialized to JSON: {"message": "..."}
        } else if (service.checkUserAlreadyExits(newUser.getUsername())){
            // And for the 403 Forbidden case
            Map<String, String> errorResponse = Collections.singletonMap("message", "Username is already taken. Please choose a different username.");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN) // 403
                    .body(errorResponse);
        }
        else {
            // Success path
            Users registeredUser = service.userAndBusinessRegister(
                    newUser,
                    newBusiness,
                    newUser.getReferredCode());
            return ResponseEntity.ok(registeredUser);
        }
    }

    //@CrossOrigin(origins = "https://app.openwaremyanmar.site", allowCredentials = "true")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            // 1) authenticate -> JWT
            String token = service.verify(user);

            // 2) resolve user + business
            Long userId = service.getUserIdByUsername(user.getUsername()); // helper shown below
            var bizInfo = businessService.getBizInfoByEmail(user.getUsername());
            Long bizId = (bizInfo != null) ? bizInfo.getBusinessId() : null;

            // 3) capture IP/UA and UPSERT the single session row for this user
            String ip = IpUtils.getClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            sessionService.upsertOnLogin(userId, ip, userAgent);

            // 4) set secure cookie
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true)       // requires HTTPS
                    .sameSite("None")   // use Lax if not cross-site
                    .path("/")
                    //.domain("openwaremyanmar.site")
                    .maxAge(Duration.ofDays(3650))
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "business", bizId
            ));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Incorrect username or password"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response) {
        // expire the cookie regardless
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true) // Match the original
                .sameSite("None") // Match the original
                .path("/") // Match the original
                //.domain("openwaremyanmar.site") // Match the original
                .maxAge(0) // Expire immediately
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // best‑effort: mark the single session row INACTIVE by userId
        try {
            String token = GetTokenFromRequest.getToken(request, request.getHeader("Authorization"));
            if (token != null && !token.isBlank()) {
                String username = jwtService.extractUsername(token);
                Long userId = service.getUserIdByUsername(username);
                if (userId != null) {
                    sessionService.markLogoutByUser(userId); // no sessionId needed
                }
            }
        } catch (Exception ignored) { /* ignore and still return OK */ }

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }



    @GetMapping("/info")
    public ResponseEntity<Users> getUser(HttpServletRequest request,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader){
        String token = GetTokenFromRequest.getToken(request, authHeader);

        String username = jwtService.extractUsername(token);
        Users user = userRepo.findByUsername(username);
        System.out.println("FOUND USER>>>: " + user);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @PutMapping("/profilePics/edit/{userId}")
    public ResponseEntity<String> editUserImage(@PathVariable Long userId,
                                           @RequestPart("profilePicture") MultipartFile profilePicture){
        return ResponseEntity.ok(service.editUserImage(userId, profilePicture));
    }

    @PutMapping("/edit/name/{userId}")
    public ResponseEntity<Users> editFullName(@PathVariable Long userId,
                                              @RequestBody Map<String, String> request) {
        String fullName = request.get("fullName");
        Users updatedUser = service.editFullName(userId, fullName);
        return ResponseEntity.ok(updatedUser);
    }

}
