package com.aunghein.SpringTemplate.service.media;

import com.aunghein.SpringTemplate.repository.media.MediaUrlRepo;
import com.aunghein.SpringTemplate.service.SupabaseService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupabaseMediaService {

    private final MediaUrlRepo mediaUrlRepo;
    private final SupabaseService supabaseService;

    @Value("${supabase.url}")
    private String SUPABASE_URL;

    @Value("${supabase.apiKey}")
    private String SUPABASE_KEY;

    @Value("${supabase.bucket}")
    private String BUCKET;



//    @Scheduled(cron = "0 0 2 * * ?") // Runs daily at 2 AM
//    public void autoDeleteOrphanedFiles() {
//        try {
//            List<String> orphanedFiles = getSupabaseFilesNotInDb();
//            if (!orphanedFiles.isEmpty()) {
//                System.out.println("Found " + orphanedFiles.size() + " orphaned files to delete");
//
//                for (String fileUrl : orphanedFiles) {
//                    try {
//                        supabaseService.deleteFile(fileUrl);
//                        System.out.println("Deleted: " + fileUrl);
//                    } catch (Exception e) {
//                        System.err.println("Failed to delete " + fileUrl + ": " + e.getMessage());
//                    }
//                }
//            } else {
//                System.out.println("No orphaned files found");
//            }
//        } catch (Exception e) {
//            System.err.println("Error in autoDeleteOrphanedFiles: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    public List<String> getSupabaseFilesNotInDb() throws Exception {
       List<String> allSupabase = listAllImages();                  // all public URLs
        List<String> dbUrls      = mediaUrlRepo.getExistingUrlBasedOnDb();

       return allSupabase.stream()
                .filter(url -> !dbUrls.contains(url))
                .collect(Collectors.toList());
    }

    public List<String> listAllImages() throws Exception {
        List<String> allImages = new ArrayList<>();

        // Add all business logos
        allImages.addAll(listFiles("business/logo"));

        // Add all group images
        allImages.addAll(listFiles("group/"));

        // Add all item images
        allImages.addAll(listFiles("group/items/"));

        // Add all profile pictures
        allImages.addAll(listFiles("profilePictures/"));

        return allImages;
    }

    /**
     * Lists all files in a specific folder path
     * @param folderPath The folder path (e.g., "business/logo")
     * @return List of public URLs
     */
    public List<String> listFiles(String folderPath) throws Exception {
        // Ensure folderPath ends with slash
        WebClient webClient = WebClient.builder().build();
        ObjectMapper objectMapper = new ObjectMapper();
        String prefix = folderPath.endsWith("/") ? folderPath : folderPath + "/";

        // Make API request
        String response = webClient.post()
                .uri(SUPABASE_URL + "/storage/v1/object/list/" + BUCKET)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + SUPABASE_KEY)
                .header("apikey", SUPABASE_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"prefix\": \"" + prefix + "\", \"limit\": 1000}")
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), res ->
                        res.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException(
                                        "Failed to list files: " + res.statusCode() + " - " + body)))
                )
                .bodyToMono(String.class)
                .block();

        // Parse response
        JsonNode root = objectMapper.readTree(response);
        if (!root.isArray() && root.has("error")) {
            throw new Exception("Supabase error: " + root.get("error").asText());
        }

        // Handle both array and object responses
        List<String> filePaths = new ArrayList<>();
        if (root.isArray()) {
            // Direct array response
            for (JsonNode node : root) {
                filePaths.add(node.get("name").asText());
            }
        } else if (root.has("objects")) {
            // Object with "objects" array
            for (JsonNode node : root.get("objects")) {
                filePaths.add(node.get("name").asText());
            }
        }

        // Convert to public URLs
        return filePaths.stream()
                .map(name -> SUPABASE_URL + "/storage/v1/object/public/" + BUCKET + "/" +
                        (prefix + name).replace("//", "/"))
                .collect(Collectors.toList());
    }


}
