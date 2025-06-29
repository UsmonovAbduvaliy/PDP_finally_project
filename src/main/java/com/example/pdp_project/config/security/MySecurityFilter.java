package com.example.pdp_project.config.security;

import com.example.pdp_project.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.pdp_project.utils.Const.TOKEN_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
public class MySecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // OPTIONS so‘rovlarini qo‘llab-quvvatlash
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:63342");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            return;
        }

        String token = extractToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtService.validateToken(token)) {
                    User user = jwtService.getUserObject(token);
                    log.info("User object from token: {}", user);

                    if (user == null || user.getId() == null) {
                        log.error("User or ID is null from token: {}", user);
                        throw new IllegalArgumentException("Invalid user data from token");
                    }

                    if (user.getIsActive() != null && !user.getIsActive()) {
                        log.warn("User is not active: {}", user.getUsername());
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("User account is not active.");
                        return;
                    }

                    var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                log.error("Invalid token: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}

