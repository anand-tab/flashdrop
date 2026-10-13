package com.flashdrop.inventoryService.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Checks the JWT issued by your Auth Service. Matches how it signs tokens:
 *   - HS256, key = jwt.secret as plain text (UTF-8 bytes)
 *   - the user's email is the token subject
 * Needs jjwt-api, jjwt-impl, jjwt-jackson (0.12.x) in this service. jwt.secret must be the
 * exact same value as in the Auth Service.
 */
@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Throws if the signature is wrong or the token has expired. */
    public String validateAndGetEmail(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }
}