package com.example.odyssey.fileandserver.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 10240);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logRequest(requestId, wrappedRequest);
            logResponse(requestId, wrappedResponse, duration);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(String requestId, ContentCachingRequestWrapper request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = queryString != null ? uri + "?" + queryString : uri;

        String headers = Collections.list(request.getHeaderNames()).stream()
                .filter(name -> !name.equalsIgnoreCase("authorization"))
                .map(name -> name + ": " + request.getHeader(name))
                .collect(Collectors.joining(", "));

        String body = getRequestBody(request);

        log.info("[{}] --> {} {} | Headers: [{}]{}",
                requestId, method, fullPath, headers,
                body.isEmpty() ? "" : " | Body: " + body);
    }

    private void logResponse(String requestId, ContentCachingResponseWrapper response, long duration) {
        int status = response.getStatus();
        String body = getResponseBody(response);

        log.info("[{}] <-- {} ({} ms){}",
                requestId, status, duration,
                body.isEmpty() ? "" : " | Body: " + body);
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }
        return sanitizeBody(new String(content, StandardCharsets.UTF_8));
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }
        return sanitizeBody(new String(content, StandardCharsets.UTF_8));
    }

    private String sanitizeBody(String body) {
        // Mask sensitive fields like password
        return body.replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"")
                   .replaceAll("\"oldPassword\"\\s*:\\s*\"[^\"]*\"", "\"oldPassword\":\"***\"")
                   .replaceAll("\"newPassword\"\\s*:\\s*\"[^\"]*\"", "\"newPassword\":\"***\"")
                   .replaceAll("\"passwordAnswer\"\\s*:\\s*\"[^\"]*\"", "\"passwordAnswer\":\"***\"");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Only log API requests
        return !path.startsWith("/api/");
    }
}
