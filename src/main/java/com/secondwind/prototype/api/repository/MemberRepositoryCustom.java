package com.secondwind.prototype.api.repository;

import com.secondwind.prototype.api.domain.request.SearchMember;
import com.secondwind.prototype.api.domain.response.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface MemberRepositoryCustom {

  Page<MemberResponse> searchMembers(SearchMember searchMember, PageRequest pageRequest);
}
