package com.secondwind.prototype.api.domain.response;

import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.common.enumerate.Authority;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@NoArgsConstructor
public class MemberResponse {
    private Long id;
    private String loginId;
    private String name;

    @Builder(builderMethodName = "of", builderClassName = "of")
    public MemberResponse(Long id, String loginId, String name) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
    }

    public static MemberResponse toResponse(Member member) {
        return MemberResponse.of()
                .id(member.getId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .build();
    }
}
