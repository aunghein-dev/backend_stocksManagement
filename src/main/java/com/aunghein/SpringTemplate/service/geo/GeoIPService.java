package com.aunghein.SpringTemplate.service.geo;


import com.aunghein.SpringTemplate.utils.IpUtils;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeoIPService {

    private final DatabaseReader databaseReader;

    public Optional<GeoInfo> lookup(String ip) {
        try {
            if (ip == null || ip.isBlank()) return Optional.empty();
            if (!IpUtils.isPublicIp(ip)) return Optional.empty(); // <— add this

            String normalized = ip.contains(",") ? ip.split(",")[0].trim() : ip.trim();
            InetAddress addr = InetAddress.getByName(normalized);
            var response = databaseReader.city(addr);

            var country = response.getCountry();
            var city = response.getCity();
            var loc = response.getLocation();

            return Optional.of(GeoInfo.builder()
                    .countryIso(country.getIsoCode())
                    .countryName(country.getName())
                    .cityName(city.getName())
                    .latitude(loc.getLatitude())
                    .longitude(loc.getLongitude())
                    .build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    @lombok.Builder
    @lombok.Value
    public static class GeoInfo {
        String countryIso;
        String countryName;
        String cityName;
        Double latitude;
        Double longitude;
    }
}