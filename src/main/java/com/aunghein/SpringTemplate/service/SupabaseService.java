package com.aunghein.SpringTemplate.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
        this.webClient = WebClient.builder().build(); // We'll use dynamic base URL per request
    }

    public String uploadFile(MultipartFile file, String folderPath) throws Exception {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String objectPath = folderPath + "/" + fileName;

        return WebClient.builder()
                .baseUrl(SUPABASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + SUPABASE_KEY)
                .defaultHeader("x-upsert", "true")
                .build()
                .post()
                .uri("/storage/v1/object/" + BUCKET + "/" + objectPath) // ✅ Correct endpoint
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // ✅ Raw bytes
                .bodyValue(file.getBytes())
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), res ->
                        res.bodyToMono(String.class)
                                .flatMap(err -> Mono.error(new RuntimeException("Supabase upload failed: " + err)))
                )
                .bodyToMono(String.class)
                .map(response -> SUPABASE_URL + "/storage/v1/object/public/" + BUCKET + "/" + objectPath) // ✅ Return public path
                .block();
    }

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

    public void deleteFile(String publicUrl) {
        String basePath = SUPABASE_URL + "/storage/v1/object/public/" + BUCKET + "/";
        if (!publicUrl.startsWith(basePath)) {
            throw new IllegalArgumentException("Invalid public URL: " + publicUrl);
        }

        String fileName = publicUrl.substring(basePath.length());
        String objectPath = "public/" + BUCKET + "/" + fileName;

        WebClient.builder()
                .baseUrl(SUPABASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + SUPABASE_KEY)
                .build()
                .delete()
                .uri("/storage/v1/object/" + objectPath)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), res ->
                        res.bodyToMono(String.class).flatMap(err -> {
                            // Only suppress 404
                            if (err.contains("\"statusCode\":\"404\"")) {
                                return Mono.empty(); // Silently ignore not found
                            }
                            return Mono.error(new RuntimeException("Supabase delete failed: " + err));
                        })
                )
                .toBodilessEntity()
                .block();
    }

}
