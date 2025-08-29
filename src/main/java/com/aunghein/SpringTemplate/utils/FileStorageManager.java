package com.aunghein.SpringTemplate.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class FileStorageManager {

    @Value("${minio.url}/public/")
    private String fileStorage;

    public static String extractUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public static String extractName(String url) {
        String fileName = extractUrl(url);
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }
}
