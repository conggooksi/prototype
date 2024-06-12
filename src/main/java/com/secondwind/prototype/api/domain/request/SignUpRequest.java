package com.secondwind.prototype.api.domain.request;

import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.common.enumerate.Authority;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@NoArgsConstructor
public class SignUpRequest {
    @NotEmpty(message = "아이디를 입력해주세요.")
    private String loginId;
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    private String password;
    private String name;

    @Builder(builderClassName = "of", builderMethodName = "of")
    public SignUpRequest(String loginId, String password, String name) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }

    public Member toMember(SignUpRequest signUpRequest, PasswordEncoder passwordEncoder) {
        return Member.of()
                .loginId(signUpRequest.getLoginId())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .name(signUpRequest.getName())
                .authority(Authority.ROLE_USER)
                .build();
    }
}
