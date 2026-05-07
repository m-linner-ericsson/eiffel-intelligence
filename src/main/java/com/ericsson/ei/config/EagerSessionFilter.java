package com.ericsson.ei.config;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Placed inside Spring Security filter chain after BasicAuthenticationFilter.
 * Creates session and sets X-Auth-Token header BEFORE the controller writes
 * the response body (which commits the response).
 */
public class EagerSessionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // Force session creation and set header before response is committed
        HttpSession session = request.getSession(true);
        response.setHeader("X-Auth-Token", session.getId());
        filterChain.doFilter(request, response);
    }
}
