package com.puang.global.security.util;

import com.puang.global.exception.ProjectException;
import com.puang.global.security.config.JwtProperties;
import com.puang.global.response.code.GeneralErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Creates and verifies JWTs. The token subject is the user id.
 * A "type" claim separates access tokens (sent on every request)
 * from refresh tokens (only sent to POST /api/auth/refresh).
 */
@Slf4j
@Component
public class JwtProvider {

    private static final String TYPE_CLAIM = "type";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    private final SecretKey key;
    private final long accessTokenExpirationMillis;
    private final long refreshTokenExpirationMillis;

    public JwtProvider(JwtProperties properties) {
        this.key = createKey(properties.secret());
        this.accessTokenExpirationMillis = properties.accessTokenExpiration().toMillis();
        this.refreshTokenExpirationMillis = properties.refreshTokenExpiration().toMillis();
    }

    private static SecretKey createKey(String secret) {
        if (secret == null || secret.isBlank()) {
            log.warn("JWT_SECRET is not set. Using a random key; tokens become invalid when the server restarts.");
            return Jwts.SIG.HS256.key().build();
        }
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 bytes long.");
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(Long userId) {
        return createToken(userId, ACCESS, accessTokenExpirationMillis);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, REFRESH, refreshTokenExpirationMillis);
    }

    private String createToken(Long userId, String type, long expirationMillis) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(TYPE_CLAIM, type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMillis))
                .signWith(key)
                .compact();
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMillis / 1000;
    }

    /**
     * Returns the user id in an access token.
     *
     * @throws ProjectException EXPIRED_TOKEN or INVALID_TOKEN
     */
    public Long getUserIdFromAccessToken(String token) {
        return getUserId(token, ACCESS);
    }

    /**
     * Returns the user id in a refresh token.
     *
     * @throws ProjectException EXPIRED_TOKEN or INVALID_TOKEN
     */
    public Long getUserIdFromRefreshToken(String token) {
        return getUserId(token, REFRESH);
    }

    private Long getUserId(String token, String expectedType) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new ProjectException(GeneralErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ProjectException(GeneralErrorCode.INVALID_TOKEN);
        }
        if (!expectedType.equals(claims.get(TYPE_CLAIM, String.class))) {
            throw new ProjectException(GeneralErrorCode.INVALID_TOKEN);
        }
        try {
            return Long.valueOf(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new ProjectException(GeneralErrorCode.INVALID_TOKEN);
        }
    }
}
