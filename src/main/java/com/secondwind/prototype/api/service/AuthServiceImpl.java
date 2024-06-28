package com.secondwind.prototype.api.service;

import com.secondwind.prototype.api.domain.request.SignUpRequest;
import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.api.domain.request.TokenRequestDTO;
import com.secondwind.prototype.api.domain.spec.PasswordSpecification;
import com.secondwind.prototype.api.repository.MemberRepository;
import com.secondwind.prototype.common.dto.TokenDTO;
import com.secondwind.prototype.common.exception.ApiException;
import com.secondwind.prototype.common.exception.CustomAuthException;
import com.secondwind.prototype.common.exception.code.AuthErrorCode;
import com.secondwind.prototype.common.exception.code.MemberErrorCode;
import com.secondwind.prototype.common.provider.JwtTokenProvider;
import com.secondwind.prototype.common.result.JsonResultData;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final PasswordSpecification passwordSpecification;
  private final PasswordEncoder passwordEncoder;
  private final MemberRepository memberRepository;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final JwtTokenProvider jwtTokenProvider;
  private final StringRedisTemplate redisTemplate;

  @Override
  public Long signup(SignUpRequest signUpRequest) {
    passwordSpecification.check(signUpRequest.getPassword());

    if (memberRepository.existsByLoginIdAndIsDeletedFalse(signUpRequest.getLoginId())) {
      throw new CustomAuthException(JsonResultData.failResultBuilder()
          .errorCode(AuthErrorCode.ALREADY_JOIN_USER.getCode())
          .errorMessage(AuthErrorCode.ALREADY_JOIN_USER.getMessage())
          .build());
    }

    Member member = SignUpRequest.toMember(signUpRequest, passwordEncoder);

    Member savedMember = memberRepository.save(member);
    return savedMember.getId();
  }

  @Override
  public TokenDTO login(String loginId, String password) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginId, password);
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    return getTokenDTO(authentication);
  }

  @Override
  public TokenDTO reissue(TokenRequestDTO tokenRequestDTO) {
    if (!jwtTokenProvider.validateToken(tokenRequestDTO.getAccessToken())) {
      throw new CustomAuthException(JsonResultData
          .failResultBuilder()
          .errorCode(AuthErrorCode.INVALID_TOKEN.getCode())
          .errorMessage(AuthErrorCode.INVALID_TOKEN.getMessage())
          .build());
    }

    Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequestDTO.getAccessToken());
    String refreshToken = redisTemplate.opsForValue().get("RT:" + tokenRequestDTO.getAccessToken());
    if (refreshToken == null || !refreshToken.equals(tokenRequestDTO.getRefreshToken())) {
      throw new CustomAuthException(JsonResultData
          .failResultBuilder()
          .errorCode(AuthErrorCode.NOT_MATCH_TOKEN_INFO.getCode())
          .errorMessage(AuthErrorCode.NOT_MATCH_TOKEN_INFO.getMessage())
          .build());
    }

    return getTokenDTO(authentication);
  }

  @Override
  public void logout(TokenRequestDTO tokenRequestDTO) {
    if (!jwtTokenProvider.validateToken(tokenRequestDTO.getAccessToken())) {
      throw new CustomAuthException(JsonResultData
          .failResultBuilder()
          .errorCode(AuthErrorCode.INVALID_TOKEN.getCode())
          .errorMessage(AuthErrorCode.INVALID_TOKEN.getMessage())
          .build());
    }

    if (redisTemplate.opsForValue().get("RT:" + tokenRequestDTO.getAccessToken()) != null) {
      redisTemplate.delete("RT:" + tokenRequestDTO.getAccessToken());
    }
  }

  @Override
  public void updatePassword(String loginId, String password) {
    Member member = memberRepository.findByLoginIdAndIsDeletedFalse(loginId)
        .orElseThrow(
            () -> ApiException.builder()
                .errorCode(MemberErrorCode.MEMBER_NOT_FOUND.getCode())
                .errorMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build());

    member.changePassword(passwordEncoder.encode(password));
  }

  private TokenDTO getTokenDTO(Authentication authentication) {
    TokenDTO tokenDTO = jwtTokenProvider.generateTokenDTO(authentication);

    redisTemplate.opsForValue()
        .set("RT:" + tokenDTO.getAccessToken(),
            tokenDTO.getRefreshToken(),
            tokenDTO.getRefreshTokenExpiresIn(),
            TimeUnit.MILLISECONDS);

    return tokenDTO;
  }
}
