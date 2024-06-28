package com.secondwind.prototype.api.repository;

import com.secondwind.prototype.api.domain.entity.Member;
import java.nio.channels.FileChannel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

  Optional<Member> findByLoginId(String loginId);

  boolean existsByLoginIdAndIsDeletedFalse(String loginId);

  Optional<Member> findByIdAndIsDeletedFalse(Long memberId);

  Optional<Member> findByLoginIdAndIsDeletedFalse(String loginId);
}
