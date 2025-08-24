package com.aunghein.SpringTemplate.config;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GeoIPConfig {

    @Value("${app.geoip.database-path}")
    private Resource dbResource;

    @Bean
    public DatabaseReader geoIpDatabaseReader() throws IOException {
        try (InputStream is = dbResource.getInputStream()) {
            // Load DB into memory; for large files, copy to temp file first
            System.out.println("[GeoIP] Loaded DB from: " + dbResource.getDescription());
            return new DatabaseReader.Builder(is).build();
        }
    }
}