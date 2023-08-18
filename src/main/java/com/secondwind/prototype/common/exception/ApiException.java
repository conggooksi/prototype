package com.secondwind.prototype.common.exception;

import com.secondwind.prototype.common.result.JsonResultData;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException{
    private JsonResultData errorEntity;
    private HttpStatus status;

    @Builder
    public ApiException(String errorMessage, String errorCode, HttpStatus status) {
        super(errorMessage);
        this.errorEntity = JsonResultData.failResultBuilder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
        this.status = status;
    }
}
