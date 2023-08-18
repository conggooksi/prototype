package com.secondwind.prototype.api.member.domain.entity;

import com.secondwind.prototype.common.enumerate.Authority;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String loginId;
    private String password;
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Authority authority;
    @Column(name = "is_deleted")
    private boolean isDeleted;
    private LocalDateTime createdAt;

    @Builder(builderMethodName = "of", builderClassName = "of")
    public Member(Long id, String loginId, String password, String nickname, Authority authority, boolean isDeleted, LocalDateTime createdAt) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.authority = authority;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }
}
