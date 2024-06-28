package com.secondwind.prototype.api.controller;


import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.api.domain.request.SearchMember;
import com.secondwind.prototype.api.domain.response.MemberResponse;
import com.secondwind.prototype.api.repository.MemberRepository;
import com.secondwind.prototype.api.service.MemberService;
import com.secondwind.prototype.common.annotation.WithMockTestUser;
import com.secondwind.prototype.common.enumerate.Authority;
import com.secondwind.prototype.common.exception.code.MemberErrorCode;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class MemberControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MemberRepository memberRepository;

  @Autowired
  private MemberService memberService;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private PasswordEncoder passwordEncoder;

  protected MediaType contentType =
      new MediaType(MediaType.APPLICATION_JSON.getType(),
          MediaType.APPLICATION_JSON.getSubtype(),
          StandardCharsets.UTF_8);

  @Test
  @WithMockTestUser(loginId = "conggooksi", password = "1234", authority = Authority.ROLE_ADMIN)
  void getMembers() throws Exception {
    // given
    Member member1 = Member.of()
        .id(1L)
        .loginId("conggooksi1")
        .password(passwordEncoder.encode("1234"))
        .name("conggooksi1")
        .authority(Authority.ROLE_USER)
        .isDeleted(false)
        .build();
    Member member2 = Member.of()
        .id(2L)
        .loginId("conggooksi2")
        .password(passwordEncoder.encode("1234"))
        .name("conggooksi2")
        .authority(Authority.ROLE_USER)
        .isDeleted(false)
        .build();
    Member member3 = Member.of()
        .id(3L)
        .loginId("conggooksi3")
        .password(passwordEncoder.encode("1234"))
        .name("conggooksi3")
        .authority(Authority.ROLE_USER)
        .isDeleted(false)
        .build();
    MemberResponse memberResponse1 = MemberResponse.toResponse(member1);
    memberResponse1.setCreatedAt(LocalDateTime.now());
    memberResponse1.setUpdatedAt(LocalDateTime.now());
    MemberResponse memberResponse2 = MemberResponse.toResponse(member2);
    memberResponse2.setCreatedAt(LocalDateTime.now());
    memberResponse2.setUpdatedAt(LocalDateTime.now());
    MemberResponse memberResponse3 = MemberResponse.toResponse(member3);
    memberResponse3.setCreatedAt(LocalDateTime.now());
    memberResponse3.setUpdatedAt(LocalDateTime.now());

    Page<MemberResponse> memberResponses = new PageImpl<>(
        List.of(memberResponse1, memberResponse2, memberResponse3));
    SearchMember searchMember = new SearchMember(10, 0, "id", Direction.ASC, null, null);
    PageRequest pageRequest = PageRequest.of(searchMember.getOffset(), searchMember.getLimit(),
        searchMember.getDirection(), searchMember.getOrderBy());

    given(memberRepository.searchMembers(any(SearchMember.class), any(PageRequest.class)))
        .willReturn(memberResponses);

    // when & then
    mockMvc.perform(
            get("/api/members")
                .contentType(contentType)
                .accept(contentType))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.data.content[0].id")
                .value(memberResponse1.getId()))
        .andExpect(
            jsonPath("$.data.content[0].loginId")
                .value(memberResponse1.getLoginId()))
        .andExpect(
            jsonPath("$.data.content[0].createdAt")
                .isNotEmpty())
        .andExpect(
            jsonPath("$.data.content[0].updatedAt")
                .isNotEmpty())
        .andExpect(
            jsonPath("$.data.content[1].id")
                .value(memberResponse2.getId()))
        .andExpect(
            jsonPath("$.data.content[1].loginId")
                .value(memberResponse2.getLoginId()))
        .andExpect(
            jsonPath("$.data.content[1].createdAt")
                .isNotEmpty())
        .andExpect(
            jsonPath("$.data.content[1].updatedAt")
                .isNotEmpty())
        .andExpect(
            jsonPath("$.data.content[2].id")
                .value(memberResponse3.getId()))
        .andExpect(
            jsonPath("$.data.content[2].loginId")
                .value(memberResponse3.getLoginId()))
        .andExpect(
            jsonPath("$.data.content[2].createdAt")
                .isNotEmpty())
        .andExpect(
            jsonPath("$.data.content[2].updatedAt")
                .isNotEmpty());

//    verify(memberRepository, times(1)).searchMembers(searchMember, pageRequest);
  }

  @Test
  void getMembers_with_searchParameter_loginId() throws Exception {
    // given
    Member member = Member.of()
        .id(1L)
        .loginId("conggooksi")
        .password(passwordEncoder.encode("1234"))
        .name("conggooksi")
        .authority(Authority.ROLE_USER)
        .isDeleted(false)
        .build();
    MemberResponse memberResponse = MemberResponse.toResponse(member);
    PageImpl<MemberResponse> memberResponses = new PageImpl<>(List.of(memberResponse));
    SearchMember searchMember = new SearchMember(10, 0, "id", Direction.ASC, "conggooksi",
        "conggooksi");
    PageRequest pageRequest = PageRequest.of(searchMember.getOffset(), searchMember.getLimit(),
        searchMember.getDirection(), searchMember.getOrderBy());

    given(memberRepository.searchMembers(any(SearchMember.class), any(PageRequest.class)))
        .willReturn(memberResponses);

    // when & then
    mockMvc.perform(
            get("/api/members")
                .contentType(contentType)
                .accept(contentType)
                .param("loginId", searchMember.getLoginId()))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.data.content[0].loginId")
                .value(memberResponse.getLoginId()));

//    verify(memberRepository, times(2)).searchMembers(searchMember, pageRequest);
  }

  @Test
  void getMembers_with_searchParameter_name() throws Exception {
    // given
    Member member = Member.of()
        .id(1L)
        .loginId("conggooksi")
        .password(passwordEncoder.encode("1234"))
        .name("conggooksi")
        .authority(Authority.ROLE_USER)
        .isDeleted(false)
        .build();
    MemberResponse memberResponse = MemberResponse.toResponse(member);
    PageImpl<MemberResponse> memberResponses = new PageImpl<>(List.of(memberResponse));
    SearchMember searchMember = new SearchMember(10, 0, "id", Direction.ASC, "conggooksi",
        "conggooksi");
    PageRequest pageRequest = PageRequest.of(searchMember.getOffset(), searchMember.getLimit(),
        searchMember.getDirection(), searchMember.getOrderBy());

    given(memberRepository.searchMembers(any(SearchMember.class), any(PageRequest.class)))
        .willReturn(memberResponses);

    // when & then
    mockMvc.perform(
            get("/api/members")
                .contentType(contentType)
                .accept(contentType)
                .param("name", searchMember.getName()))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.data.content[0].name")
                .value(memberResponse.getName()));

//    verify(memberRepository, times(1)).searchMembers(searchMember, pageRequest);
  }

  @Test
  @WithMockTestUser(loginId = "conggooksi", password = "1234", authority = Authority.ROLE_ADMIN)
  void getMember_success() throws Exception {
    // given
    Member member = Member.of()
        .id(1L)
        .loginId("conggooksi")
        .password(passwordEncoder.encode("1234"))
        .name("나다")
        .authority(Authority.ROLE_ADMIN)
        .build();

    given(memberRepository.findByIdAndIsDeletedFalse(anyLong())).willReturn(
        Optional.of(member));
    MemberResponse memberResponse = MemberResponse.toResponse(member);

    // when & then
    mockMvc.perform(
            get("/api/members/{memberId}", 1L)
                .contentType(contentType)
                .accept(contentType))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.data.id")
                .value(memberResponse.getId()))
        .andExpect(
            jsonPath("$.data.loginId")
                .value(memberResponse.getLoginId()))
        .andExpect(
            jsonPath("$.data.name")
                .value(memberResponse.getName()))
        .andExpect(
            jsonPath("$.data.authority")
                .value(memberResponse.getAuthority().toString()));
  }

  @Test
  @WithMockTestUser(loginId = "conggooksi", password = "1234", authority = Authority.ROLE_ADMIN)
  void getMember_fail() throws Exception {
    // given
    Member member = Member.of()
        .id(1L)
        .loginId("conggooksi")
        .password(passwordEncoder.encode("1234"))
        .name("나다")
        .authority(Authority.ROLE_ADMIN)
        .build();

    given(memberRepository.findById(1L)).willReturn(Optional.of(member));

    // when & then
    mockMvc.perform(
            get("/api/members/{memberId}", 2L)
                .contentType(contentType)
                .accept(contentType))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error.message",
                is(MemberErrorCode.MEMBER_NOT_FOUND.getMessage())));
//        .andExpect(
//            jsonPath("$.error.")
//                .value(memberResponse.getId()));
  }
}