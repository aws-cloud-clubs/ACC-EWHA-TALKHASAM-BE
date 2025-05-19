package com.talkhasam.artichat.global.config;

import com.talkhasam.artichat.global.security.CustomAuthenticationFilter;
import com.talkhasam.artichat.global.security.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private HttpSecurity http;
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;

    // 1. PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. AuthenticationManager: 패스워드 null 허용하는 DaoAuthenticationProvider 등록
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) throws Exception {
        this.http = http;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider() {
            @Override
            protected void additionalAuthenticationChecks(
                    UserDetails userDetails,
                    UsernamePasswordAuthenticationToken authentication
            ) throws AuthenticationException {
                if (userDetails.getPassword() == null) {
                    // 패스워드가 없으면 체크 스킵
                    return;
                }
                super.additionalAuthenticationChecks(userDetails, authentication);
            }
        };
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider)
                .build();
    }

    // 3. SecurityFilterChain: 로그인 필터 + 토큰 검증 필터 등록
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            AuthenticationManager authManager,
            CustomAuthenticationFilter tokenFilter,
            LoginFilter loginFilter
    ) throws Exception {
        // 로그인 필터 설정
        loginFilter.setAuthenticationManager(authManager);
        loginFilter.setFilterProcessesUrl("/chatuser/login");      // 로그인 엔드포인트
        loginFilter.setAuthenticationSuccessHandler((req, res, auth) -> {
            String token = tokenService.generateToken(auth.getName());
            res.setHeader("X-AUTH-TOKEN", token);
        });

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/chatuser/login").permitAll()
                                .anyRequest().authenticated()
                )
                // 순서 주의: 로그인 처리 필터 먼저, 그 다음 토큰 검증 필터
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
