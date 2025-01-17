package com.FreeBoard.FreeBoard_Profile_Spring.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {

    // Секретный ключ для подписи JWT. Его важно хранить в безопасности!
    private static final String SECRET_KEY = "e3f7e72a3b6945d8b519b0c2358b9454f0334b2c0b6e3d6db5b44c6be7413b8f";

    /**
     * Извлекает email пользователя (subject) из JWT токена.
     *
     * @param token JWT токен
     * @return email пользователя
     */
    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Универсальный метод для извлечения указанного claim из токена.
     *
     * @param token          JWT токен
     * @param claimsResolver Функция, извлекающая нужный claim из объекта Claims
     * @return Значение claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Генерация токена без дополнительных данных.
     *
     * @param userDetails Информация о пользователе
     * @return JWT токен
     */
    public  String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Генерация JWT токена с дополнительными claims.
     *
     * @param extraClaims Дополнительные данные
     * @param userDetails Информация о пользователе
     * @return JWT токен
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userEmail = extractUserEmail(token);
        return (userEmail.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает все claims (данные) из токена.
     *
     * @param token JWT токен
     * @return Объект Claims с данными
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey()) // Передайте правильный ключ для проверки подписи
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Преобразует секретный ключ в объект Key для подписи/проверки токенов.
     *
     * @return Ключ для HMAC-SHA
     */
    private Key getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}