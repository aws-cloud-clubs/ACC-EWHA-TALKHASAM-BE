package com.talkhasam.artichat.global.security;


public interface TokenService {
    /** 주어진 유저네임으로 토큰 생성 */
    String generate(String username);

    /** 토큰 유효성 검증 */
    boolean validate(String token);

    /** 토큰으로부터 유저네임 추출 */
    String extractUsername(String token);
}
