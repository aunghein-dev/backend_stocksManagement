package com.aunghein.SpringTemplate.config;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Configuration
public class GeoIPConfig {

    @Value("${app.geoip.database-path}")
    private String databasePath;

    @Bean
    public DatabaseReader geoIpDatabaseReader() throws IOException {
        Resource resource;

        if (databasePath.startsWith("/") || databasePath.startsWith("file:")) {
            // Absolute file path
            File file = databasePath.startsWith("file:") ?
                    new File(URI.create(databasePath)) :
                    new File(databasePath);
            return new DatabaseReader.Builder(file).build();
        } else {
            // Classpath fallback
            resource = new ClassPathResource(databasePath);
            return new DatabaseReader.Builder(resource.getInputStream()).build();
        }
    }
}
