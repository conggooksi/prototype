package com.secondwind.prototype.api.domain.response;

import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.common.enumerate.Authority;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class MemberResponse {

  private Long id;
  private String loginId;
  private String name;
  private Authority authority;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Builder(builderMethodName = "of", builderClassName = "of")
  public MemberResponse(Long id, String loginId, String name, Authority authority, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.loginId = loginId;
    this.name = name;
    this.authority = authority;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static MemberResponse toResponse(Member member) {
    return MemberResponse.of()
        .id(member.getId())
        .loginId(member.getLoginId())
        .name(member.getName())
        .authority(member.getAuthority())
        .createdAt(member.getCreatedAt())
        .updatedAt(member.getUpdatedAt())
        .build();
  }
}
