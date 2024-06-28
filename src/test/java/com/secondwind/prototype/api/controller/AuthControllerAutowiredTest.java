package com.secondwind.prototype.api.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.api.domain.request.SignUpRequest;
import com.secondwind.prototype.api.domain.request.TokenRequestDTO;
import com.secondwind.prototype.api.repository.MemberRepository;
import com.secondwind.prototype.api.service.AuthService;
import com.secondwind.prototype.api.service.AuthServiceImpl;
import com.secondwind.prototype.api.service.CustomUserDetailsService;
import com.secondwind.prototype.common.dto.TokenDTO;
import com.secondwind.prototype.common.enumerate.Authority;
import com.secondwind.prototype.common.exception.code.AuthErrorCode;
import com.secondwind.prototype.common.exception.code.MemberErrorCode;
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
import org.springframework.web.servlet.View;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class AuthControllerAutowiredTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AuthService authService;

  @Autowired
  private MemberRepository memberRepository;

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

  @Test
  void login_success() throws Exception {
    // given
    String loginId = "conggooksi";
    String password = "1234";
    String authBasic = Base64.getEncoder().encodeToString((loginId + ":" + password).getBytes());
    Member member = Member.of()
//        .id(1L)
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .name("나다")
        .authority(Authority.ROLE_USER)
        .build();
    memberRepository.save(member);

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
        .andExpect(status().isOk());
  }
}