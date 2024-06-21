package com.secondwind.prototype.common.annotation;

import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.api.repository.MemberRepository;
import com.secondwind.prototype.common.provider.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WithMockUserSecurityContextFactory implements WithSecurityContextFactory<WithMockTestUser> {

  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  @Override
  public SecurityContext createSecurityContext(WithMockTestUser annotation) {
    Member member = Member.of()
//        .id(annotation.id())
        .loginId(annotation.loginId())
        .password(passwordEncoder.encode(annotation.password()))
//        .name(annotation.name())
        .authority(annotation.authority())
//        .isDeleted(annotation.isDeleted())
        .build();

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        annotation.loginId(), annotation.password(), Collections.singleton(new SimpleGrantedAuthority(annotation.authority().toString())));
    context.setAuthentication(authenticationToken);

    return context;
  }
}
