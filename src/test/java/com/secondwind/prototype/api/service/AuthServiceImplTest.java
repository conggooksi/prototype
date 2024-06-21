package com.secondwind.prototype.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.api.domain.entity.MemberMock;
import com.secondwind.prototype.api.domain.request.SignUpRequest;
import com.secondwind.prototype.api.domain.spec.PasswordSpecification;
import com.secondwind.prototype.api.repository.MemberRepository;
import com.secondwind.prototype.common.exception.ApiException;
import com.secondwind.prototype.common.exception.CustomAuthException;
import com.secondwind.prototype.common.exception.code.AuthErrorCode;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordSpecification passwordSpecification;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  void signUpRequest_success() throws NoSuchFieldException, IllegalAccessException {
    // given
    SignUpRequest request = SignUpRequest.of()
        .loginId("conggooksi")
        .name("나다")
        .password("1234")
        .build();

    doAnswer(invocation -> null).when(passwordSpecification).check(request.getPassword());
    Member member = MemberMock.createMember(request);
    Field idField = member.getClass().getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(member, 1L);

    given(memberRepository.save(any(Member.class))).willReturn(member);
//    when(memberRepository.save(any(Member.class))).thenReturn(member);

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

    doAnswer(invocation -> true).when(passwordSpecification).check(request.getPassword());
    Member member = MemberMock.createMember(request);

    given(memberRepository.existsByLoginIdAndIsDeletedFalse(request.getLoginId())).willReturn(true);

    // when
    CustomAuthException customAuthException = assertThrows(CustomAuthException.class,
        () -> authService.signup(request));

    // then
    assertEquals(AuthErrorCode.ALREADY_JOIN_USER.getCode(),
        customAuthException.getErrorEntity().getError().getCode());
  }

  @Test
  void signUpRequest_failed_with_invalid_password() {
    // given
    SignUpRequest request = SignUpRequest.of()
        .loginId("conggooksi")
        .name("나다")
        .password("123")
        .build();

    when(SignUpRequest.toMember(request, passwordEncoder)).thenThrow(ApiException.builder()
        .errorCode(AuthErrorCode.PASSWORD_NOT_ENOUGH_CONDITION.getCode())
        .errorMessage(AuthErrorCode.PASSWORD_NOT_ENOUGH_CONDITION.getMessage())
        .status(HttpStatus.BAD_REQUEST)
        .build());

    // when
    ApiException apiException = assertThrows(ApiException.class,
        () -> authService.signup(request));

    // then
    assertEquals(AuthErrorCode.PASSWORD_NOT_ENOUGH_CONDITION.getCode(), apiException.getErrorEntity().getError().getCode());
  }

}