package com.secondwind.prototype.api.member.domain.dto;

import com.secondwind.prototype.api.member.domain.entity.Member;
import com.secondwind.prototype.common.enumerate.Authority;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@NoArgsConstructor
public class MemberJoinDTO {
    private String loginId;
    private String password;
    private String nickname;

    @Builder(builderClassName = "of", builderMethodName = "of")
    public MemberJoinDTO(String loginId, String password, String nickname) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
    }

    public Member toMember(MemberJoinDTO memberJoinDTO, PasswordEncoder passwordEncoder) {
        return Member.of()
                .loginId(memberJoinDTO.getLoginId())
                .password(passwordEncoder.encode(memberJoinDTO.getPassword()))
                .nickname(memberJoinDTO.getNickname())
                .authority(Authority.ROLE_USER)
                .build();
    }
}
