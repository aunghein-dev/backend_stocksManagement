package com.aunghein.SpringTemplate.controller.media;

import com.aunghein.SpringTemplate.service.media.SupabaseMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class SupabaseMediaController {

    private final SupabaseMediaService supabaseMediaService;

    @Value("${media.cleanup.secret}")
    private String cleanupSecret;

    @PostMapping("/run-now/{secret}")
    public ResponseEntity<String> runCleanupNow(@PathVariable String secret) {
        if (!secret.equals(cleanupSecret)) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        supabaseMediaService.autoDeleteOrphanedFiles();
        return ResponseEntity.ok("Cleanup process triggered");
    }

    @PostMapping("/dry-run/{secret}")
    public ResponseEntity<List<String>> dryRun(@PathVariable String secret) {
        if (!secret.equals(cleanupSecret)) {
            return ResponseEntity.status(403).build();
        }
        try {
            List<String> orphanedFiles = supabaseMediaService.getSupabaseFilesNotInDb();
            return ResponseEntity.ok(orphanedFiles);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/openware/media/v0/{secret}")
    public ResponseEntity<List<String>> getSupabaseFilesNotInDb(@PathVariable String secret) throws Exception {
        if (!secret.equals(cleanupSecret)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(supabaseMediaService.getSupabaseFilesNotInDb());
    }
}
