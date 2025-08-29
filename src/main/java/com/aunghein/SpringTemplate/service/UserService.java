package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.Business;
import com.aunghein.SpringTemplate.model.ReferredCode;
import com.aunghein.SpringTemplate.model.Users;
import com.aunghein.SpringTemplate.model.dto.TellerResponse;
import com.aunghein.SpringTemplate.repository.BusinessRepo;
import com.aunghein.SpringTemplate.repository.ReferredCodeRepo;
import com.aunghein.SpringTemplate.repository.UserRepo;
import com.aunghein.SpringTemplate.service.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final BusinessRepo businessRepo;
    private final JWTService jwtService;
    private final AuthenticationManager authManager;
    private final SupabaseService supabaseService;
    private final ReferredCodeRepo codeRepo;
    private final MinioService minioService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users register(Users user){
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public List<Users> getUsers() {
        return userRepo.findAll();
    }

    public String verify(Users user) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(
                        user.getUsername(), user.getPassword()
                ));
        return authentication.isAuthenticated()? jwtService.generateToken(user.getUsername()) : "fail";
    }

    @Transactional
    public String editUserImage(Long userId, MultipartFile profilePicture) {
        Users toEditUser = userRepo.findById(userId)
                .orElseThrow();

        if (toEditUser.getUserImgUrl() != null && toEditUser.getUserImgUrl().startsWith("https://svmeynesalueoxzhtdqp.supabase.co")) {
            supabaseService.deleteFile(toEditUser.getUserImgUrl());
        } else {
            // Handle the case where userImgUrl is null or doesn't start with the Supabase URL
            // For example, log a message, or do nothing if it's an expected scenario.
            System.out.println("User image URL is null or not a Supabase URL, no deletion needed.");
        }

        /*
        //DELETE
        if (toEditUser.getUserImgUrl() != null && toEditUser.getUserImgUrl().contains("file.openwaremyanmar")) {
            try {
                minioService.deleteFile(toEditUser.getUserImgUrl());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            // Handle the case where userImgUrl is null or doesn't start with the Supabase URL
            // For example, log a message, or do nothing if it's an expected scenario.
            System.out.println("User image URL is null or not a Supabase URL, no deletion needed.");
        }
        */

        String newUrl = "";
        try {
            newUrl = supabaseService.uploadProfilePictures(profilePicture);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        /*
        //UPLOAD
        String newUrl = "";
        try {
            newUrl = minioService.uploadFile(profilePicture);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        */

        toEditUser.setUserImgUrl(newUrl);

        userRepo.save(toEditUser);

        return newUrl;
    }

    @Transactional
    public Users editFullName(Long userId, String newFullName) {
        Users toEditUser = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId)); // Provide a more specific exception

        toEditUser.setFullName(newFullName); // Set the actual full name
        userRepo.save(toEditUser);
        return toEditUser;
    }

    public boolean checkIsUsedSecretCode(String secretCode) {
        return codeRepo.isAlreadyUsedByCode(secretCode);
    }

    public int hasValueCode(String secretCode) {
        Integer count = codeRepo.hasValueCode(secretCode);
        return (count != null) ? count : 0;
    }


    @Transactional // Ensure this method runs within a transaction
    public Users userAndBusinessRegister(Users newUser, Business newBusiness, String secretCode) {

        // 1. Mark the secret code as used
        ReferredCode oldReferredCode = codeRepo.findByCode(secretCode);
        if (oldReferredCode == null) {
            // Handle case where secret code is not found (should be caught by checkIsUsedSecretCode if implemented correctly)
            throw new IllegalArgumentException("Secret code not found.");
        }
        oldReferredCode.setUsedBy(newUser.getUsername());
        oldReferredCode.setIsUsed(true);
        codeRepo.save(oldReferredCode);

        // 2. Save the new Business entity first
        newBusiness.setBusinessLogo(null); // Assuming logo handled separately or defaulted
        newBusiness.setRegisteredBy(newUser.getUsername());
        newBusiness.setBusinessNameShortForm(null);
        Business savedBusiness = businessRepo.save(newBusiness); // Save and get the object with the generated ID

        // 3. Assign the saved Business to the new User
        newUser.setUserImgUrl(null); // Assuming user image handled separately or defaulted
        newUser.setBusiness(savedBusiness); // Assign the business with its newly generated ID
        newUser.setPassword(encoder.encode(newUser.getPassword()));

        // 4. Save the new User entity
        Users savedUser = userRepo.save(newUser); // <-- YOU WERE MISSING THIS LINE!

        return savedUser; // Return the saved user object
    }

    public boolean checkUserAlreadyExits(String username) {
        return userRepo.existsByUsername(username);
    }

    public Users resetPassword(Long id, String newPassword) {
        Users user = userRepo.findById(id).orElseThrow();
        user.setPassword(encoder.encode(newPassword));
        return userRepo.save(user);
    }

    public Long getUserIdByUsername(String username) {
        Users u = userRepo.findByUsername(username);
        return (u != null) ? u.getId() : null;
    }
}
