package com.secondwind.prototype.api.service;

import com.secondwind.prototype.api.domain.request.SearchMember;
import com.secondwind.prototype.api.domain.response.MemberResponse;
import org.springframework.data.domain.Page;

public interface MemberService {

  Page<MemberResponse> getMembers(SearchMember searchMember);

  MemberResponse getMember(Long memberId);
}
