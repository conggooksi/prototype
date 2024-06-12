package com.secondwind.prototype.api.service;

import com.secondwind.prototype.api.domain.request.SignUpRequest;
import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.api.domain.spec.PasswordSpecification;
import com.secondwind.prototype.api.repository.MemberRepository;
import com.secondwind.prototype.common.exception.CustomAuthException;
import com.secondwind.prototype.common.exception.code.AuthErrorCode;
import com.secondwind.prototype.common.result.JsonResultData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final PasswordSpecification passwordSpecification;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public Long signup(SignUpRequest signUpRequest) {
        passwordSpecification.check(signUpRequest.getPassword());

        if (memberRepository.existsByLoginIdAndIsDeletedFalse(signUpRequest.getLoginId())) {
            throw new CustomAuthException(JsonResultData.failResultBuilder()
                    .errorCode(AuthErrorCode.ALREADY_JOIN_USER.getCode())
                    .errorMessage(AuthErrorCode.ALREADY_JOIN_USER.getMessage())
                    .build());
        }

        Member member = signUpRequest.toMember(signUpRequest, passwordEncoder);

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }
}
