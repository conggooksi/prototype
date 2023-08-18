package com.secondwind.prototype.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondwind.prototype.common.exception.code.AuthErrorCode;
import com.secondwind.prototype.common.result.JsonResultData;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = request.getAttribute("exception").toString();

        if (exception == null) {
            setResponse(response, AuthErrorCode.UNKNOWN_ERROR);
        } else if (exception.equals(AuthErrorCode.NOT_MATCH_PASSWORD.getCode())) {
            setResponse(response, AuthErrorCode.NOT_MATCH_PASSWORD);
        } else if (exception.equals(AuthErrorCode.WRONG_TYPE_TOKEN.getCode())) {
            setResponse(response, AuthErrorCode.WRONG_TYPE_TOKEN);
        } else if (exception.equals((AuthErrorCode.EXPIRED_TOKEN.getCode()))) {
            setResponse(response, AuthErrorCode.EXPIRED_TOKEN);
        } else if (exception.equals(AuthErrorCode.UNSUPPORTED_TOKEN.getCode())) {
            setResponse(response, AuthErrorCode.UNSUPPORTED_TOKEN);
        } else {
            setResponse(response, AuthErrorCode.ACCESS_DENIED);
        }
    }

    private void setResponse(HttpServletResponse response, AuthErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonResultData<Object> data = JsonResultData.failResultBuilder()
                .errorCode(errorCode.getCode())
                .errorMessage(errorCode.getMessage())
                .build();

        response.getWriter().print(objectMapper.writeValueAsString(data));
    }
}
