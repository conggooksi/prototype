package com.secondwind.prototype.api.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.api.domain.request.SignUpRequest;
import com.secondwind.prototype.api.domain.request.TokenRequestDTO;
import com.secondwind.prototype.api.repository.MemberRepository;
import com.secondwind.prototype.api.service.CustomUserDetailsService;
import com.secondwind.prototype.common.dto.TokenDTO;
import com.secondwind.prototype.common.enumerate.Authority;
import com.secondwind.prototype.common.exception.code.AuthErrorCode;
import com.secondwind.prototype.common.provider.JwtTokenProvider;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import jdk.jfr.Description;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.servlet.View;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MemberRepository memberRepository;

  @MockBean
  private CustomUserDetailsService customUserDetailsService;

  @MockBean
  private StringRedisTemplate redisTemplate;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @MockBean
  private ValueOperations<String, String> valueOperations;
  private final ObjectMapper objectMapper = new ObjectMapper();


  @Autowired
  private PasswordEncoder passwordEncoder;

  protected MediaType contentType =
      new MediaType(MediaType.APPLICATION_JSON.getType(),
          MediaType.APPLICATION_JSON.getSubtype(),
          StandardCharsets.UTF_8);
  @Autowired
  private View error;

  @Test
  @Description("회원가입하면 일단 역할은 ROLE_USER로 설정한다.")
  void signup_success() throws Exception {
    // given
    String loginId = "conggooksi";
    String password = "1234";
    String authBasic = Base64.getEncoder().encodeToString((loginId + ":" + password).getBytes());
    SignUpRequest signUpRequest = SignUpRequest.of()
        .name("나다")
        .build();
    Member member = Member.of()
        .id(1L)
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .name("나다")
        .authority(Authority.ROLE_USER)
        .build();

    String requestBody = objectMapper.writeValueAsString(signUpRequest);
    given(memberRepository.save(any(Member.class))).willReturn(member);

    // when & then
    mockMvc.perform(post("/api/auth/signup")
            .contentType(contentType)
            .accept(contentType)
            .header(HttpHeaders.AUTHORIZATION, "Basic " + authBasic)
            .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data").value(1L));
  }

  @Test
  void signup_failed_no_header() throws Exception {
    // given
    SignUpRequest signUpRequest = SignUpRequest.of()
        .name("나다")
        .build();

    String requestBody = objectMapper.writeValueAsString(signUpRequest);
//    given(memberRepository.save(any(Member.class))).willReturn(member);

    // when & then
    mockMvc.perform(post("/api/auth/signup")
            .contentType(contentType)
            .accept(contentType)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.message",
            is(AuthErrorCode.ENTERED_ID_AND_PASSWORD.getMessage())));
  }

  @Test
  void signup_failed_duplicated_loginId() throws Exception {
    // given
    String loginId = "conggooksi";
    String password = "1234";
    String authBasic = Base64.getEncoder().encodeToString((loginId + ":" + password).getBytes());
    SignUpRequest signUpRequest = SignUpRequest.of()
        .name("나다")
        .build();

    String requestBody = objectMapper.writeValueAsString(signUpRequest);
    given(memberRepository.existsByLoginIdAndIsDeletedFalse(anyString())).willReturn(true);

    // when & then
    mockMvc.perform(post("/api/auth/signup")
            .contentType(contentType)
            .accept(contentType)
            .header(HttpHeaders.AUTHORIZATION, "Basic " + authBasic)
            .content(requestBody))
        .andExpect(status().isUnauthorized())
        .andExpect(
            jsonPath("$.error.message",
                is(AuthErrorCode.ALREADY_JOIN_USER.getMessage())));
  }

  @Test
  void login_success() throws Exception {
    // given
    String loginId = "conggooksi";
    String password = "1234";
    String authBasic = Base64.getEncoder().encodeToString((loginId + ":" + password).getBytes());
    Member member = Member.of()
        .id(1L)
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .name("나다")
        .authority(Authority.ROLE_USER)
        .build();
    given(customUserDetailsService.loadUserByUsername(anyString()))
        .willReturn(new User(
            member.getId().toString(),
            member.getPassword(),
            Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().toString()))));
    given(redisTemplate.opsForValue())
        .willReturn(valueOperations);
    given(redisTemplate.opsForValue().get(anyString()))
        .willReturn("refreshToken");
    given(jwtTokenProvider.generateTokenDTO(any()))
        .willReturn(TokenDTO.of()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .accessTokenExpiresIn(1000L)
            .refreshTokenExpiresIn(1000L)
            .build());

    // when & then
    mockMvc.perform(post("/api/auth/login")
            .contentType(contentType)
            .accept(contentType)
            .header(HttpHeaders.AUTHORIZATION, "Basic " + authBasic))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.data.accessToken")
                .isString())
        .andExpect(
            jsonPath("$.data.refreshToken")
                .isString())
        .andExpect(
            jsonPath("$.data.accessTokenExpiresIn")
                .isNumber())
        .andExpect(
            jsonPath("$.data.refreshTokenExpiresIn")
                .isNumber());
  }

  @Test
  void login_failed_not_found_member() throws Exception {
    // given
    String loginId = "conggooksi";
    String password = "1234";
    String authBasic = Base64.getEncoder().encodeToString((loginId + ":" + password).getBytes());
    Member member = Member.of()
        .id(1L)
        .loginId("conggooksi22222")
        .password(passwordEncoder.encode(password))
        .name("나다")
        .authority(Authority.ROLE_USER)
        .build();
    given(customUserDetailsService.loadUserByUsername(anyString()))
        .willThrow(new UsernameNotFoundException(loginId + "아이디 또는 비밀번호를 확인해주세요."));

    // when & then
    mockMvc.perform(post("/api/auth/login")
            .contentType(contentType)
            .accept(contentType)
            .header(HttpHeaders.AUTHORIZATION, "Basic " + authBasic))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void reissueTest() throws Exception{
    // given
    TokenRequestDTO tokenRequestDTO =
        new TokenRequestDTO(
            "accessToken",
            "refreshToken");

    given(jwtTokenProvider.validateToken(anyString()))
        .willReturn(true);
    given(redisTemplate.opsForValue())
        .willReturn(valueOperations);
    given(redisTemplate.opsForValue().get(anyString()))
        .willReturn("refreshToken");
    given(jwtTokenProvider.generateTokenDTO(any()))
        .willReturn(TokenDTO.of()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .accessTokenExpiresIn(1000L)
            .refreshTokenExpiresIn(1000L)
            .build());

    String requestBody = objectMapper.writeValueAsString(tokenRequestDTO);

    // when & then
    mockMvc.perform(
        post("/api/auth/reissue")
            .contentType(contentType)
            .accept(contentType)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.data.accessToken")
                .isString())
        .andExpect(
            jsonPath("$.data.refreshToken")
                .isString());
  }

  @Test
  void logoutTest() throws Exception{
    // given
    TokenRequestDTO tokenRequestDTO =
        new TokenRequestDTO(
            "accessToken",
            "refreshToken");

    given(jwtTokenProvider.validateToken(anyString()))
        .willReturn(true);
    given(redisTemplate.opsForValue())
        .willReturn(valueOperations);
    given(redisTemplate.opsForValue().get(anyString()))
        .willReturn("refreshToken");

    String requestBody = objectMapper.writeValueAsString(tokenRequestDTO);

    // when & then
    mockMvc.perform(
        post("/api/auth/logout")
            .contentType(contentType)
            .accept(contentType)
            .content(requestBody))
        .andExpect(status().isOk());
  }

  @Test
  void updatePasswordTest() throws Exception {
    // given
    String loginId = "conggooksi";
    String password = "asdf";
    String authBasic = Base64.getEncoder().encodeToString((loginId + ":" + password).getBytes());

    given(memberRepository.findByLoginId(anyString()))
        .willReturn(Optional.ofNullable(Member.of()
            .id(1L)
            .loginId(loginId)
            .password("password")
            .name("나다")
            .authority(Authority.ROLE_USER)
            .build()));

    // when & then
    mockMvc.perform(
            patch("/api/auth/password")
                .contentType(contentType)
                .accept(contentType)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + authBasic))
        .andExpect(status().isOk());
  }
}