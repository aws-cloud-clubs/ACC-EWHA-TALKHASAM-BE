package com.talkhasam.artichat.global.security;

// 커스텀 토큰 발급/검증/파싱 서비스 인터페이스
public interface TokenService {
    // 주어진 사용자명을 기반으로 토큰 생성
    String generateToken(String username);

    // 토큰 유효성 검증
    boolean validate(String token);

    // 토큰으로부터 사용자명(또는 식별키) 추출
    String extractUsername(String token);
}


