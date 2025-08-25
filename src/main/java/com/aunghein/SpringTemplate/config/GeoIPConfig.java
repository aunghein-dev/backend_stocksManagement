package com.aunghein.SpringTemplate.config;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class GeoIPConfig {

    // Default to the classpath copy if nothing is set
    @Value("${app.geoip.database-path:classpath:GeoLite2-City.mmdb}")
    private String databasePath;

    @Autowired private ResourceLoader resourceLoader;

    @Bean
    public DatabaseReader geoIpDatabaseReader() throws IOException {
        String path = databasePath == null ? "" : databasePath.trim();

        // Support Spring-style locations: classpath:, file:, etc.
        Resource resource = resourceLoader.getResource(path);

        // If it's a real file and exists, use it
        if (path.startsWith("file:") || (Paths.get(path).isAbsolute() && Files.exists(Paths.get(path)))) {
            File file = path.startsWith("file:")
                    ? new File(URI.create(path))
                    : Paths.get(path).toFile();
            return new DatabaseReader.Builder(file).build();
        }

        // Otherwise, try to treat it like a Resource (classpath, etc.)
        if (!resource.exists()) {
            // final fallback: look for a classpath resource with the plain name
            resource = resourceLoader.getResource("classpath:GeoLite2-City.mmdb");
        }

        try (InputStream in = resource.getInputStream()) {
            return new DatabaseReader.Builder(in).build();
        }
    }
}
