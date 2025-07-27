package com.aunghein.SpringTemplate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.UUID;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.reactive.function.client.WebClientRequestException; // Import this specific exception
import java.io.IOException; // Also good to retry on general IOExceptions

@Service
public class SupabaseService {

    @Value("${supabase.url}")
    private String SUPABASE_URL;

    @Value("${supabase.apiKey}")
    private String SUPABASE_KEY;

    @Value("${supabase.bucket}")
    private String BUCKET;

    private final WebClient webClient;

    public SupabaseService() {
        // It's generally better to inject WebClient or use a single instance
        // if configuration allows, but for this specific issue, the current
        // approach of building a new one per request is not the root cause.
        this.webClient = WebClient.builder().build();
    }

    /**
     * Uploads a file to Supabase Storage.
     * This method is configured to retry on network-related exceptions.
     *
     * @param file The MultipartFile to upload.
     * @param folderPath The folder path within the Supabase bucket (e.g., "group/", "profilePictures/").
     * @return The public URL of the uploaded file.
     * @throws Exception if the upload fails after all retries.
     */
    @Retryable(
            value = { WebClientRequestException.class, IOException.class }, // Retry on these exceptions
            maxAttempts = 3, // Try up to 3 times (initial attempt + 2 retries)
            backoff = @Backoff(delay = 1000, multiplier = 2) // Wait 1 second, then 2 seconds, then 4 seconds before retrying
    )
    public String uploadFile(MultipartFile file, String folderPath) throws Exception {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String objectPath = folderPath + "/" + fileName;

        System.out.println("Attempting to upload file: " + objectPath); // Log for retry visibility

        return WebClient.builder()
                .baseUrl(SUPABASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + SUPABASE_KEY)
                .defaultHeader("x-upsert", "true")
                .build()
                .post()
                .uri("/storage/v1/object/" + BUCKET + "/" + objectPath)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(file.getBytes())
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), res ->
                        res.bodyToMono(String.class)
                                .flatMap(err -> {
                                    System.err.println("Supabase upload non-2xx status: " + res.statusCode() + ", Error: " + err);
                                    return Mono.error(new RuntimeException("Supabase upload failed with status " + res.statusCode() + ": " + err));
                                })
                )
                .bodyToMono(String.class)
                .map(response -> SUPABASE_URL + "/storage/v1/object/public/" + BUCKET + "/" + objectPath)
                .block();
    }

    // The following methods will automatically benefit from the retry logic
    // because they call the 'uploadFile' method.
    public String uploadGroupImage(MultipartFile file) throws Exception {
        return uploadFile(file, "group/");
    }

    public String uploadItemImage(MultipartFile file) throws Exception {
        return uploadFile(file, "group/items");
    }

    public String uploadBusinessLogo(MultipartFile file) throws Exception {
        return uploadFile(file, "business/logo");
    }

    public String uploadProfilePictures(MultipartFile file) throws Exception {
        return uploadFile(file, "profilePictures/");
    }

    /**
     * Deletes a file from Supabase Storage.
     * This method does not have retry logic applied by default as deletion
     * is often idempotent and a "not found" error is explicitly handled.
     * If deletion also experiences "Connection reset" errors, you could
     * apply @Retryable here too.
     *
     * @param publicUrl The public URL of the file to delete.
     */
    public void deleteFile(String publicUrl) {
        String basePath = SUPABASE_URL + "/storage/v1/object/public/" + BUCKET + "/";
        // Validate the public URL
        if (!publicUrl.startsWith(basePath) || publicUrl.equals("https://svmeynesalueoxzhtdqp.supabase.co/storage/")) {
            System.out.println("Skipping invalid or base public URL: " + publicUrl);
            return; // Gracefully return instead of throwing
        }

        // Extract the object path from the public URL, correctly handling the "public/" prefix for deletion.
        // Supabase delete endpoint expects the path relative to the bucket, not including "public/bucketName/".
        // The original code had "public/" + BUCKET + "/" + fileName which is incorrect for the delete URI.
        // It should be just the path within the bucket.
        String objectPathWithinBucket = publicUrl.substring(basePath.length());


        WebClient.builder()
                .baseUrl(SUPABASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + SUPABASE_KEY)
                .build()
                .delete()
                // The delete URI should be /storage/v1/object/bucketName/path/to/file.ext
                .uri("/storage/v1/object/" + BUCKET + "/" + objectPathWithinBucket)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), res ->
                        res.bodyToMono(String.class).flatMap(err -> {
                            System.err.println("Supabase delete non-2xx status: " + res.statusCode() + ", Error: " + err);
                            // Only suppress 404 (Not Found) for delete operations
                            if (res.statusCode().value() == 404 || err.contains("\"statusCode\":\"404\"")) {
                                System.out.println("File not found during delete, silently ignoring: " + publicUrl);
                                return Mono.empty(); // Silently ignore not found
                            }
                            return Mono.error(new RuntimeException("Supabase delete failed with status " + res.statusCode() + ": " + err));
                        })
                )
                .toBodilessEntity()
                .block();
    }
}
