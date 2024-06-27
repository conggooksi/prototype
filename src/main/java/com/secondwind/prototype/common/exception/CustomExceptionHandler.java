package com.secondwind.prototype.common.exception;

import com.secondwind.prototype.common.exception.code.AuthErrorCode;
import com.secondwind.prototype.common.result.ResponseHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({ApiException.class})
    public ResponseEntity<?> exceptionHandler(HttpServletRequest request, final ApiException e) {
        return ResponseHandler.failResultGenerate()
                .status(e.getStatus())
                .errorMessage(e.getErrorEntity().getError().getMessage())
                .errorCode(e.getErrorEntity().getError().getCode())
                .build();
    }

    @ExceptionHandler({MissingRequestHeaderException.class})
    public ResponseEntity<?> exceptionHandler(HttpServletRequest request, final MissingRequestHeaderException e) {
        return ResponseHandler.failResultGenerate()
            .errorMessage(AuthErrorCode.ENTERED_ID_AND_PASSWORD.getMessage())
            .errorCode(AuthErrorCode.ENTERED_ID_AND_PASSWORD.getCode())
            .status(HttpStatus.BAD_REQUEST)
            .build();
    }

    @ExceptionHandler({CustomAuthException.class})
    public ResponseEntity<?> exceptionHandler(HttpServletRequest request, final CustomAuthException e) {
        return ResponseHandler.failResultGenerate()
                .status(HttpStatus.UNAUTHORIZED)
                .errorMessage(e.getErrorEntity().getError().getMessage())
                .errorCode(e.getErrorEntity().getError().getCode())
                .build();
    }
}
