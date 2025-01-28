package com.sgi.cloud_gateway.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface TokenProvider {
    UsernamePasswordAuthenticationToken validateAuthentication(String token) throws Exception;
}
