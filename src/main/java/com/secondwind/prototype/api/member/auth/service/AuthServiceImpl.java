package com.secondwind.prototype.api.member.auth.service;

import com.secondwind.prototype.api.member.domain.dto.MemberJoinDTO;
import com.secondwind.prototype.api.member.domain.entity.Member;
import com.secondwind.prototype.api.member.domain.spec.PasswordSpecification;
import com.secondwind.prototype.api.member.repository.MemberRepository;
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
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final PasswordSpecification passwordSpecification;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long signup(MemberJoinDTO memberJoinDTO) {
        passwordSpecification.check(memberJoinDTO.getPassword());

        if (memberRepository.existsByLoginIdAndIsDeletedFalse(memberJoinDTO.getLoginId())) {
            throw new CustomAuthException(JsonResultData.failResultBuilder()
                    .errorCode(AuthErrorCode.ALREADY_JOIN_USER.getCode())
                    .errorMessage(AuthErrorCode.ALREADY_JOIN_USER.getMessage())
                    .build());
        }

        Member member = memberJoinDTO.toMember(memberJoinDTO, passwordEncoder);


        return memberRepository.save(member).getId();
    }
}
