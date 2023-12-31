package com.secondwind.prototype.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.StringJoiner;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggerInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (request.getClass().getName().contains("SecurityContextHolderAwareRequestWrapper")) return;
        if (request.getMethod().equals(HttpMethod.GET.name())) return;

        StringJoiner logContent = new StringJoiner(" ");

        logContent.add(request.getMethod());
        logContent.add(request.getRequestURI());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            logContent.add("Request User :").add(authentication.getName());
        }

        final ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
        final ContentCachingResponseWrapper cachingResponse = (ContentCachingResponseWrapper) response;

        if (cachingRequest.getContentType() != null && cachingRequest.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
            if (cachingRequest.getContentAsByteArray().length != 0) {
                logContent.add("Request Body :");
                logContent.add(objectMapper.readTree(cachingRequest.getContentAsByteArray()).toString());
            }
        }

        if (cachingResponse.getContentType() != null && cachingResponse.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
            if (cachingResponse.getContentAsByteArray().length != 0) {
                logContent.add("Response Body :");
                logContent.add(objectMapper.readTree(cachingResponse.getContentAsByteArray()).toString());
            }
        }

        log.info(logContent.toString());
    }
}
