package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.Customer;
import com.aunghein.SpringTemplate.model.dto.CustomerDashboard;
import com.aunghein.SpringTemplate.repository.CustomerRepo;
import com.aunghein.SpringTemplate.service.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepo customerRepo;
    private final SupabaseService supabaseService;
    //private final MinioService minioService;

    public Customer createNewCustomer(Long bizId, Customer newCustomer, MultipartFile customerFileImg) {
        String url = null;
        try {
            url = supabaseService.uploadGroupImage(customerFileImg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        //UPLOAD
        String url2 = null;
        try {
            url2 = minioService.uploadFile(customerFileImg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        newCustomer.setImgUrl(url);
        newCustomer.setBizId(bizId);
        newCustomer.setRowId(null);

        customerRepo.save(newCustomer);
        return newCustomer;
    }

    public List<Customer> findAllByBizId(Long bizId) {
        return customerRepo.findAllByBizId(bizId);
    }

    @Transactional // Ensures atomicity of the operation
    public Customer updateExistingCustomer(Long bizId, Customer updatedCustomerData, MultipartFile customerFileImg) {
        // 1. Retrieve the existing customer from the database
        // This is crucial to ensure you're updating an actual record and to get its current image URL.
        Customer existingCustomer = customerRepo.findByCidAndBizId(updatedCustomerData.getCid(), bizId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + updatedCustomerData.getCid()));

        // 2. Handle Image Update Logic
        if (customerFileImg != null && !customerFileImg.isEmpty()) {
            // A new image file is provided
            try {
                // If there's an old image from Supabase, delete it first
                if (existingCustomer.getImgUrl() != null && existingCustomer.getImgUrl().startsWith("https://svmeynesalueoxzhtdqp.supabase.co")) {
                    supabaseService.deleteFile(existingCustomer.getImgUrl());
                }
                // Upload the new image
                String newImageUrl = supabaseService.uploadGroupImage(customerFileImg);
                updatedCustomerData.setImgUrl(newImageUrl);

                /*
                //DELETE
                if (existingCustomer.getImgUrl() != null && existingCustomer.getImgUrl().contains("file.openwaremyanmar")){
                    minioService.deleteFile(existingCustomer.getImgUrl());
                }
                //UPLOAD
                String newImageUrl = minioService.uploadFile(customerFileImg);
                updatedCustomerData.setImgUrl(newImageUrl);
                */

            } catch (Exception e) {
                // Log the exception properly
                System.err.println("Failed to upload new customer image: " + e.getMessage());
                // Consider throwing a custom exception or handling gracefully
                throw new RuntimeException("Error uploading customer image", e);
            }
        } else if (updatedCustomerData.getImgUrl() == null || updatedCustomerData.getImgUrl().isEmpty()) {
            // No new file provided, and the client explicitly sent null/empty for imgUrl
            // This indicates the client wants to remove the existing image
            if (existingCustomer.getImgUrl() != null && existingCustomer.getImgUrl().startsWith("https://svmeynesalueoxzhtdqp.supabase.co")) {
                supabaseService.deleteFile(existingCustomer.getImgUrl());
            }

            /*
            //DELETE
            if (existingCustomer.getImgUrl() != null && existingCustomer.getImgUrl().contains("file.openwaremyanmar")){
                try {
                    minioService.deleteFile(existingCustomer.getImgUrl());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            */

            updatedCustomerData.setImgUrl(null); // Explicitly set to null
        } else {
            // No new file provided, and client did NOT send null/empty for imgUrl.
            // This means the client didn't change the image, so keep the existing one.
            // We ensure the existingCustomer's imgUrl is maintained if not explicitly cleared by the client.
            updatedCustomerData.setImgUrl(existingCustomer.getImgUrl());
        }

        // 3. Update other fields from updatedCustomerData to existingCustomer
        // This is important to ensure only allowed fields are updated and to prevent
        // accidental overwrites of fields not meant to be updated via this method.
        existingCustomer.setName(updatedCustomerData.getName());
        existingCustomer.setTypeOfCustomer(updatedCustomerData.getTypeOfCustomer());
        existingCustomer.setAddress1(updatedCustomerData.getAddress1());
        existingCustomer.setPhoneNo1(updatedCustomerData.getPhoneNo1());
        existingCustomer.setPhoneNo2(updatedCustomerData.getPhoneNo2());
        existingCustomer.setTownship(updatedCustomerData.getTownship());
        existingCustomer.setCity(updatedCustomerData.getCity());
        existingCustomer.setCustomerDueAmt(updatedCustomerData.getCustomerDueAmt());
        existingCustomer.setCustomerLastDueDate(updatedCustomerData.getCustomerLastDueDate());
        existingCustomer.setLastShopDate(updatedCustomerData.getLastShopDate());
        existingCustomer.setBoughtAmt(updatedCustomerData.getBoughtAmt());
        existingCustomer.setBoughtCnt(updatedCustomerData.getBoughtCnt());

        // Always set bizId if it's a required field for association
        existingCustomer.setBizId(bizId);

        // The imgUrl is already handled above, but if you're directly mapping properties
        // from updatedCustomerData, ensure imgUrl is set from updatedCustomerData.
        existingCustomer.setImgUrl(updatedCustomerData.getImgUrl()); // Set the potentially new/null image URL

        // 4. Save the updated existing customer
        return customerRepo.save(existingCustomer);
    }

    @Transactional
    public Customer deleteCustomer(Long bizId, String cid) {
        // 1. Find the customer by cid and bizId
        Optional<Customer> customerOptional = customerRepo.findByCidAndBizId(cid, bizId);

        if (customerOptional.isEmpty()) {
            // Customer not found, throw a specific HTTP error
            // This will result in a 404 Not Found response
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with ID: " + cid + " for business ID: " + bizId);
        }

        Customer customerToDelete = customerOptional.get();

        // 2. Handle image deletion (if applicable, based on your previous discussion)
        if (customerToDelete.getImgUrl() != null && customerToDelete.getImgUrl().startsWith("https://svmeynesalueoxzhtdqp.supabase.co")) {
            try {
                supabaseService.deleteFile(customerToDelete.getImgUrl());
            } catch (Exception e) {
                // Log the error but don't prevent customer deletion if image deletion fails
                // (You might want to implement a retry mechanism or a dead-letter queue for failed deletions)
                System.err.println("Failed to delete customer image from Supabase for customer CID: " + cid + ", Error: " + e.getMessage());
                // Optionally re-throw if image deletion is critical to the operation
                // throw new RuntimeException("Image deletion failed", e);
            }
        }

        /*
        //DELETE
        if (customerToDelete.getImgUrl() != null && customerToDelete.getImgUrl().contains("file.openwaremyanmar")){
            try {
                minioService.deleteFile(customerToDelete.getImgUrl());
            } catch (Exception e) {
                System.err.println("Failed to delete customer image from Supabase for customer CID: " + cid + ", Error: " + e.getMessage());
            }
        }
        */

        // 3. Delete the customer from the database
        customerRepo.delete(customerToDelete);

        return customerToDelete; // Or return a confirmation message/status
    }

    public CustomerDashboard getCustomerDashboard(Long bizId) {
        CustomerDashboard customerDashboard = customerRepo.getCustomerDashboard(bizId);
        return customerDashboard;
    }
}
