package com.aunghein.SpringTemplate.service.geo;

import com.aunghein.SpringTemplate.model.Session;
import org.springframework.stereotype.Component;

@Component
public class DeviceDetector {

    public Session.DeviceType detectType(String userAgent) {
        if (userAgent == null) return Session.DeviceType.UNKNOWN;
        String ua = userAgent.toLowerCase();
        if (ua.contains("bot") || ua.contains("crawl") || ua.contains("spider"))
            return Session.DeviceType.BOT;
        if (ua.contains("tablet") || ua.contains("ipad"))
            return Session.DeviceType.TABLET;
        if (ua.contains("mobi") || ua.contains("iphone") || ua.contains("android"))
            return Session.DeviceType.MOBILE;
        if (ua.contains("windows") || ua.contains("macintosh") || ua.contains("linux"))
            return Session.DeviceType.DESKTOP;
        return Session.DeviceType.UNKNOWN;
    }

    public String deviceName(String ua) {
        if (ua == null) return "Unknown";
        // very rough "nice" label; refine if you want UA parsing lib (uap-java)
        if (ua.contains("Chrome")) return "Chrome";
        if (ua.contains("Safari") && !ua.contains("Chrome")) return "Safari";
        if (ua.contains("Firefox")) return "Firefox";
        if (ua.contains("Edge")) return "Edge";
        return "Unknown";
    }
}