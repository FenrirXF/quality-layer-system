package com.quality.util;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final String SECRET_KEY = "quality2026BackendSecretKey888";
    private static final long EXPIRE_TIME = 2 * 60 * 60 * 1000; //令牌有效期 2 小时

    /**
     * 生成token，role、region为Long数字类型
     */
    public static String generateToken(String username, String role, String region) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);
        claims.put("region", region);

        long currentTime = System.currentTimeMillis();
        Date expireDate = new Date(currentTime + EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Claims parseToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims == null ? null : claims.get("username", String.class);
    }

    // 读取Long类型role
    public static Long getRole(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("role", Long.class);
    }

    // 读取Long类型region
    public static Long getRegion(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("region", Long.class);
    }
}