package com.sgi.cloud_gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgi.cloud_gateway.service.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Autowired
    TokenProvider tokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        try {
            String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.replace("Bearer ", "");
                UsernamePasswordAuthenticationToken authentication = tokenProvider.validateAuthentication(token);

                if (authentication != null) {
                    SecurityContext securityContext = new SecurityContextImpl(authentication);
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                }
            }
            return chain.filter(exchange);
        }catch (Exception e){
            return sendErrorResponse(exchange);
        }
    }


    private Mono<Void> sendErrorResponse(ServerWebExchange exchange) {
        return Mono.defer(() -> {
            ObjectMapper objectMapper = new ObjectMapper();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", HttpStatus.UNAUTHORIZED.value());
            errorResponse.put("message", "Expired or invalid token");
            try {
                byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
            } catch (Exception ex) {
                return Mono.error(ex);
            }
        });
    }
}
