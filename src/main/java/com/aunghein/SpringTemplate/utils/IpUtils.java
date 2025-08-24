package com.aunghein.SpringTemplate.utils;

import jakarta.servlet.http.HttpServletRequest;

public final class IpUtils {
    private IpUtils(){}

    public static String getClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For","X-Real-IP","CF-Connecting-IP","True-Client-IP",
                "X-Client-IP","X-Forwarded","Forwarded-For","Forwarded"
        };
        for (String h : headers) {
            String v = request.getHeader(h);
            if (v != null && !v.isBlank() && !"unknown".equalsIgnoreCase(v)) {
                // XFF may contain a list. Take the first non-private/public-looking one.
                for (String part : v.split(",")) {
                    String candidate = part.trim();
                    if (isPublicIp(candidate)) return candidate;
                }
                // if all were private, fall through
            }
        }
        String remote = request.getRemoteAddr();
        return remote;
    }

    public static boolean isPublicIp(String ip) {
        if (ip == null || ip.isBlank()) return false;
        ip = ip.trim();

        // IPv6 loopback
        if ("::1".equals(ip)) return false;
        // IPv4 loopback
        if ("127.0.0.1".equals(ip)) return false;

        // Strip port if present (rare with proxies): "1.2.3.4:5678"
        int colon = ip.indexOf(':');
        if (colon > -1 && ip.indexOf(':', colon + 1) == -1) { // single colon = IPv4:port
            ip = ip.substring(0, colon);
        }

        // Quick checks for private IPv4 ranges
        if (ip.startsWith("10.") ||
                ip.startsWith("192.168.") ||
                ip.matches("^172\\.(1[6-9]|2\\d|3[0-1])\\..*")) {
            return false;
        }

        // Unique local IPv6 (fc00::/7), link-local fe80::/10
        if (ip.toLowerCase().startsWith("fc") || ip.toLowerCase().startsWith("fd") ||
                ip.toLowerCase().startsWith("fe80")) {
            return false;
        }

        return true; // treat the rest as public
    }
}
