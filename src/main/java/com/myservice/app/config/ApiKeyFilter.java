package com.myservice.app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Guards the /api/v1/data/cache/** endpoints with a shared secret header.
 *
 * Callers must include:
 *   X-Cache-Key: <value of app.cache.api-key>
 *
 * Returns 401 Unauthorized if the header is missing or wrong.
 * All other routes are passed through untouched.
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFilter.class);
    private static final String CACHE_PATH_PREFIX = "/api/v1/data/cache";
    private static final String HEADER_NAME       = "X-Cache-Key";

    @Value("${app.cache.api-key}")
    private String expectedApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!path.startsWith(CACHE_PATH_PREFIX)) {
            // Not a cache endpoint — let it through
            filterChain.doFilter(request, response);
            return;
        }

        String providedKey = request.getHeader(HEADER_NAME);

        if (providedKey == null || !providedKey.equals(expectedApiKey)) {
            log.warn("Rejected cache request from {} — invalid or missing {}", request.getRemoteAddr(), HEADER_NAME);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"success\":false,\"message\":\"Unauthorized: valid X-Cache-Key header required\"}"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
