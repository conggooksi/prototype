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
  private Authority authority;

  @Builder(builderMethodName = "of", builderClassName = "of")
  public MemberResponse(Long id, String loginId, String name, Authority authority) {
    this.id = id;
    this.loginId = loginId;
    this.name = name;
    this.authority = authority;
  }

  public static MemberResponse toResponse(Member member) {
    return MemberResponse.of()
        .id(member.getId())
        .loginId(member.getLoginId())
        .name(member.getName())
        .authority(member.getAuthority())
        .build();
  }
}
