//package com.secondwind.prototype.common.config;
//
//import com.secondwind.prototype.common.filter.JwtAuthenticationFilter;
//import com.secondwind.prototype.common.provider.JwtTokenProvider;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.DefaultSecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
/*
  .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class);
  SecurityConfig 수정으로 인하여 위의 로직이 필요없어 일단 주석.
  향후 에러 발생하지 않으면 삭제 예정
* */
//@RequiredArgsConstructor
//public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
//    private final JwtTokenProvider jwtTokenProvider;
//    private final StringRedisTemplate redisTemplate;
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        JwtAuthenticationFilter customFilter = new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate);
//        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
//    }
//}
