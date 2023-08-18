package com.secondwind.prototype.common.exception.code;

import lombok.Getter;

@Getter
public enum MemberErrorCode {
    ENTERED_ID_AND_PASSWORD("ENTERED_ID_AND_PASSWORD", "아이디와 비밀번호를 입력해주세요.");
    private final String code;
    private final String message;

    MemberErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
