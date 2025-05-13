package com.wojet.pmtool.security.jwt;
import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    
    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;
    
    @Value("${jwt.secret}")
    private String jwtSecret;

    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");

        logger.debug("Authorization header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String generateTokenFromUsername(UserDetails userDetails) {
        String userName = userDetails.getUsername();
        return Jwts.builder()
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            System.out.println("Validate");
            Jwts.parser()
                .verifyWith((SecretKey)key())
                .build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Cannot set user authentication: {}", e);
        } catch (ExpiredJwtException e) {
            logger.error("Jwt token is expired: {}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Jwt token is not supported: {}", e);
        } catch (IllegalArgumentException e) {
            logger.error("Jwt token illegal argument: {}", e);
        } 
        return false; 
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
            .verifyWith((SecretKey)key())
            .build().parseSignedClaims(token)
            .getPayload()
            .getSubject();  
    }

    public Key key() {
        return Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(jwtSecret)
        );
    }

}
