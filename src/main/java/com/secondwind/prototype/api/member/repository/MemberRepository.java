package com.secondwind.prototype.api.member.repository;

import com.secondwind.prototype.api.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {
    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginIdAndIsDeletedFalse(String loginId);
}
