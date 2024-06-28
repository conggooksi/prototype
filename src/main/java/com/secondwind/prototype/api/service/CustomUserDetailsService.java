package com.secondwind.prototype.api.service;

import com.secondwind.prototype.api.domain.entity.Member;
import com.secondwind.prototype.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginIdAndIsDeletedFalse(loginId)
            .orElseThrow(() -> new UsernameNotFoundException(loginId + "아이디 또는 비밀번호를 확인해주세요."));
        return createUserDetails(member);
    }

    private UserDetails createUserDetails(Member member) {
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getAuthority().toString());
        return new User(
                member.getId().toString(),
                member.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
