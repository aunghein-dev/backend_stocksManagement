package com.aunghein.SpringTemplate.controller;

import com.aunghein.SpringTemplate.controller.Utils.GetTokenFromRequest;
import com.aunghein.SpringTemplate.model.Business;
import com.aunghein.SpringTemplate.model.StkGroup;
import com.aunghein.SpringTemplate.model.Users;
import com.aunghein.SpringTemplate.repository.UserRepo;
import com.aunghein.SpringTemplate.service.JWTService;
import com.aunghein.SpringTemplate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepo userRepo;

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

    @CrossOrigin(origins = "https://app.openwaremyanmar.site", allowCredentials = "true")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user, HttpServletResponse response) {
        try {
            String token = service.verify(user);

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None") // REQUIRED for cross-domain cookies
                    .path("/")
                    .domain("openwaremyanmar.site") // MUST be the root domain
                    .maxAge(Duration.ofHours(24))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(Map.of("message", "Login successful"));
        } catch (BadCredentialsException ex) {
            // Password incorrect or user not found
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Incorrect username or password"));
        } catch (Exception ex) {
            // General fallback
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true) // Match the original
                .sameSite("None") // Match the original
                .path("/") // Match the original
                .domain("openwaremyanmar.site") // Match the original
                .maxAge(0) // Expire immediately
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Logged out successfully");
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
