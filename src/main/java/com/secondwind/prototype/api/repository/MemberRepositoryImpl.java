package com.secondwind.prototype.api.repository;


import static com.secondwind.prototype.api.domain.entity.QMember.member;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.api.domain.request.SearchMember;
import com.secondwind.prototype.api.domain.response.MemberResponse;
import com.secondwind.prototype.common.support.Querydsl4RepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

public class MemberRepositoryImpl extends Querydsl4RepositorySupport implements
    MemberRepositoryCustom {

  public MemberRepositoryImpl() {
    super(Member.class);
  }

  @Override
  public Page<MemberResponse> searchMembers(SearchMember searchMember, PageRequest pageRequest) {
    return applyPagination(pageRequest, contentQuery ->
            contentQuery.select(Projections.constructor(MemberResponse.class,
                    member.id,
                    member.loginId,
                    member.name))
                .from(member)
                .where(loginIdEq(searchMember.getLoginId()),
                    nameEq(searchMember.getName())),
        countQuery ->
            countQuery.selectFrom(member)
                .where(loginIdEq(searchMember.getLoginId()),
                    nameEq(searchMember.getName())));

  }

  private BooleanExpression nameEq(String name) {
    return StringUtils.hasText(name) ? member.name.eq(name) : null;
  }

  private BooleanExpression loginIdEq(String loginId) {
    return StringUtils.hasText(loginId) ? member.loginId.eq(loginId) : null;
  }
}
