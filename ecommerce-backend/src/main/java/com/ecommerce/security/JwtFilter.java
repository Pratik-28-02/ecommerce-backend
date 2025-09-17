package com.ecommerce.security;

import com.ecommerce.service.CustomUserDetailsService;
import com.ecommerce.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userId = null;

        System.out.println("Request URL: " + request.getRequestURI() + ", Auth Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Extracted Token: " + token);
            try {
                userId = jwtUtil.extractUserId(token);
                System.out.println("Extracted UserId: " + userId);
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
                    System.out.println("Loaded UserDetails: " + userDetails.getUsername());
                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("Authentication Set for User: " + userId);
                    } else {
                        System.out.println("Token Validation Failed");
                    }
                }
            } catch (Exception e) {
                System.out.println("JWT Error: " + e.getMessage());
            }
        } else {
            System.out.println("No Valid Bearer Token Found");
        }
        chain.doFilter(request, response);
    }
}
