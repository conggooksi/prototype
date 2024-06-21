package com.secondwind.prototype.api.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.api.domain.entity.MemberMock;
import com.secondwind.prototype.api.domain.request.SignUpRequest;
import com.secondwind.prototype.api.domain.response.MemberResponse;
import com.secondwind.prototype.api.domain.spec.PasswordSpecification;
import com.secondwind.prototype.api.repository.MemberRepository;
import com.secondwind.prototype.common.exception.CustomAuthException;
import com.secondwind.prototype.common.exception.code.AuthErrorCode;
import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordSpecification passwordSpecification;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private MemberServiceImpl memberService;

  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  void getMemberTest() {
    // given
    SignUpRequest signUpRequest = new SignUpRequest("conggooksi", "1234", "나다");
    Member member = MemberMock.createMember(signUpRequest);
    given(memberRepository.findById(anyLong()))
        .willReturn(Optional.of(member));

    // when
    MemberResponse dbMember = memberService.getMember(1L);

    // then
    assertEquals("conggooksi", dbMember.getLoginId());
    assertEquals("나다", dbMember.getName());
  }

  @Test
  void signUpRequest_success() throws NoSuchFieldException, IllegalAccessException {
    // given
    SignUpRequest request = SignUpRequest.of()
        .loginId("conggooksi")
        .name("나다")
        .password("1234")
        .build();

    doAnswer(invocation -> true).when(passwordSpecification).check(request.getPassword());
    Member member = MemberMock.createMember(request);
    Field idField = member.getClass().getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(member, 1L);

    given(memberRepository.save(any(Member.class))).willReturn(member);

    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

    // when
    Long memberId = authService.signup(request);

    // then
    verify(memberRepository, times(1)).save(captor.capture());
    Member savedMember = captor.getValue();
    assertEquals(1L, memberId);
    assertEquals("conggooksi", savedMember.getLoginId());
    assertEquals("나다", savedMember.getName());
  }

  @Test
  void signUpRequest_failed_with_duplicated() {
    // given
    SignUpRequest request = SignUpRequest.of()
        .loginId("conggooksi")
        .name("나다")
        .password("1234")
        .build();

//    doAnswer(invocation -> true).when(passwordSpecification).check(request.getPassword());
    SignUpRequest signUpRequest = new SignUpRequest("conggooksi", "1234", "나다");
//    Member member = MemberMock.createMember(signUpRequest);
    given(memberRepository.existsByLoginIdAndIsDeletedFalse(anyString()))
        .willReturn(true);
//    given(memberRepository.save(any(Member.class)))
//        .willReturn(member);

    // when
    CustomAuthException exception = assertThrows(CustomAuthException.class,
        () -> authService.signup(request));

    // then
    assertEquals(AuthErrorCode.ALREADY_JOIN_USER.getMessage(), exception.getMessage());
  }
}