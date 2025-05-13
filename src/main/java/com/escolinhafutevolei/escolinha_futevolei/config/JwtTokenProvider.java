package com.escolinhafutevolei.escolinha_futevolei.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    @PostConstruct
    protected void init() {
        System.out.println("Valor de jwt.secret: " + secretKey);
        System.out.println("Valor de jwt.expiration: " + validityInMilliseconds);
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("A propriedade 'jwt.secret' não pode ser nula ou vazia. Configure-a em application.properties.");
        }
    }

    public String generateToken(Authentication authentication) {
        String email = authentication.getName();
        return generateTokenByEmail(email);
    }

    public String generateTokenByEmail(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}