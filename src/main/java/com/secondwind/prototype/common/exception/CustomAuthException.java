package com.secondwind.prototype.common.exception;

import com.secondwind.prototype.common.result.JsonResultData;
import lombok.Getter;

@Getter
public class CustomAuthException extends RuntimeException{
    private JsonResultData errorEntity;

    public CustomAuthException(JsonResultData errorEntity) {
        super(errorEntity.getError().getMessage());
        this.errorEntity = errorEntity;
    }
}
