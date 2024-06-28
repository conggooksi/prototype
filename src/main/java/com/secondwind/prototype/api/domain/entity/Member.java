package com.secondwind.prototype.api.domain.entity;

import com.secondwind.prototype.common.entity.BaseEntity;
import com.secondwind.prototype.common.enumerate.Authority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Member extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String loginId;
    private String password;
    private String name;
    @Enumerated(EnumType.STRING)
    private Authority authority;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Builder(builderMethodName = "of", builderClassName = "of")
    public Member(Long id, String loginId, String password, String name, Authority authority, boolean isDeleted) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.authority = authority;
        this.isDeleted = false;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
