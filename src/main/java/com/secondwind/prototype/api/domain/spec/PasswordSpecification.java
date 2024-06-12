package com.secondwind.prototype.api.domain.spec;

import com.secondwind.prototype.common.exception.ApiException;
import com.secondwind.prototype.common.exception.code.AuthErrorCode;
import com.secondwind.prototype.common.spec.AbstractSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PasswordSpecification extends AbstractSpecification<String> {
    @Override
    public boolean isSatisfiedBy(String password) {
        return password.length() >= 4;
    }

    @Override
    public void check(String password) throws ApiException {
        if (!isSatisfiedBy(password)) {
            throw ApiException.builder()
                    .errorCode(AuthErrorCode.PASSWORD_NOT_ENOUGH_CONDITION.getCode())
                    .errorMessage(AuthErrorCode.PASSWORD_NOT_ENOUGH_CONDITION.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }
}
