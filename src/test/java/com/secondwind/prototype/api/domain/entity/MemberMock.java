package com.secondwind.prototype.api.domain.entity;

import com.secondwind.prototype.api.domain.request.SignUpRequest;
import com.secondwind.prototype.common.enumerate.Authority;

public class MemberMock {

  public static Member createMember(SignUpRequest signUpRequest) {
    return Member.of()
        .loginId(signUpRequest.getLoginId())
        .password(signUpRequest.getPassword())
        .name(signUpRequest.getName())
        .authority(Authority.ROLE_USER)
        .isDeleted(false)
        .build();
  }

}
