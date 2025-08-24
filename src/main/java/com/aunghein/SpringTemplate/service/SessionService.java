package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.Session;
import com.aunghein.SpringTemplate.repository.SessionRepo;
import com.aunghein.SpringTemplate.service.geo.DeviceDetector;
import com.aunghein.SpringTemplate.service.geo.GeoIPService;
import com.aunghein.SpringTemplate.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepo sessionRepo;
    private final GeoIPService geoIPService;
    private final DeviceDetector deviceDetector;

    @Transactional
    public Session upsertOnLogin(Long userId, String ip, String userAgent) {
        var deviceType = deviceDetector.detectType(userAgent);
        var deviceName = deviceDetector.deviceName(userAgent);

        String geoRegion = null, geoCity = null;
        Double lat = null, lon = null;

        var geoOpt = geoIPService.lookup(ip);
        if (geoOpt.isPresent()) {
            var g = geoOpt.get();
            geoRegion = g.getCountryIso() != null ? g.getCountryIso() : g.getCountryName();
            geoCity = g.getCityName();
            lat = g.getLatitude();
            lon = g.getLongitude();
        } else if (!IpUtils.isPublicIp(ip)) {
            // Friendly fallback for local/dev or private networks
            geoRegion = "LOCAL";
            geoCity = "Localhost";
        }

        Session s = sessionRepo.findByUserId(userId).orElseGet(Session::new);

        s.setUserId(userId);
        s.setLoginAt(Instant.now());
        s.setLogoutAt(null);
        s.setStatus(Session.Status.ACTIVE);
        s.setIpAddress(ip);
        s.setUserAgent(userAgent);
        s.setDeviceType(deviceType);
        s.setDeviceName(deviceName);
        s.setGeoRegion(geoRegion);
        s.setGeoCity(geoCity);
        s.setLatitude(lat);
        s.setLongitude(lon);
        s.setUpdatedAt(Instant.now());

        return sessionRepo.save(s);
    }


    @Transactional
    public Optional<Session> markLogoutByUser(Long userId) {
        return sessionRepo.findByUserId(userId).map(s -> {
            s.setLogoutAt(Instant.now());
            s.setStatus(Session.Status.INACTIVE);
            s.setUpdatedAt(Instant.now());
            return sessionRepo.save(s);
        });
    }

    // If you still want the old signature:
    @Transactional
    public Optional<Session> markLogout(Long sessionId, Long userId) {
        // both variants end up at the same single row
        return sessionRepo.findByUserId(userId).map(s -> {
            s.setLogoutAt(Instant.now());
            s.setStatus(Session.Status.INACTIVE);
            s.setUpdatedAt(Instant.now());
            return sessionRepo.save(s);
        });
    }
}