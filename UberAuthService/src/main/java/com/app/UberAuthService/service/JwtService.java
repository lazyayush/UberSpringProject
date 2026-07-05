package com.app.UberAuthService.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.expiry}")
    private int expiry;

    @Value("${jwt.secret}")
    private String SECRET;

    public Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String role, String email){
        return Jwts.builder()
                .claims(Map.of("userId", userId, "role", role))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .subject(email)
                .signWith(getSignKey())
                .compact();
    }

    public String extractUsername(String token){
        return Jwts.parser().setSigningKey(getSignKey())
                .build().parseClaimsJws(token)
                .getBody().getSubject();
    }

    public Boolean validateToken(String token, String email){
        final String userEmailFromToken = extractUsername(token);
        return (userEmailFromToken.equals(email) && !isExpired(token));
    }

    public Boolean isExpired(String token){
        Date expiration = Jwts.parser().setSigningKey(getSignKey())
                .build().parseClaimsJws(token)
                .getBody().getExpiration();

        return expiration.before(new Date());
    }

}
