package com.secondwind.prototype.api.service;

import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.api.domain.request.SearchMember;
import com.secondwind.prototype.api.domain.response.MemberResponse;
import com.secondwind.prototype.api.repository.MemberRepository;
import com.secondwind.prototype.common.exception.ApiException;
import com.secondwind.prototype.common.exception.code.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService{
  private final MemberRepository memberRepository;

  @Override
  public Page<MemberResponse> getMembers(SearchMember searchMember) {
    PageRequest pageRequest = PageRequest.of(searchMember.getOffset(), searchMember.getLimit(), searchMember.getDirection(), searchMember.getOrderBy());
    return memberRepository.searchMembers(searchMember, pageRequest);
  }

  @Override
  public MemberResponse getMember(Long memberId) {
    Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
        .orElseThrow(
            () -> ApiException.builder()
                .errorMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage())
                .errorCode(MemberErrorCode.MEMBER_NOT_FOUND.getCode())
                .status(HttpStatus.BAD_REQUEST)
                .build());
    return MemberResponse.toResponse(member);
  }
}
