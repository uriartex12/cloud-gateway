package com.sgi.cloud_gateway.service.impl;

import com.sgi.cloud_gateway.service.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class JwtService implements TokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    public UsernamePasswordAuthenticationToken validateAuthentication(String token) throws Exception{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

    }
}

