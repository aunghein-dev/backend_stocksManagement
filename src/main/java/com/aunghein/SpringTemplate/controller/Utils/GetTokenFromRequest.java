package com.aunghein.SpringTemplate.controller.Utils;

import jakarta.servlet.http.HttpServletRequest;

public class GetTokenFromRequest {

    public static String getToken(HttpServletRequest request, String authHeader) {
        String token = null;

        // 1. Try from cookie
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 2. Try from Authorization header
        if (token == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty token");
        }
        return token;
    }
}
