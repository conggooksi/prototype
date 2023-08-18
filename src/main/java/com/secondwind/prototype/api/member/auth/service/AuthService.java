package com.secondwind.prototype.api.member.auth.service;

import com.secondwind.prototype.api.member.domain.dto.MemberJoinDTO;

public interface AuthService {
    Long signup(MemberJoinDTO memberJoinDTO);
}
