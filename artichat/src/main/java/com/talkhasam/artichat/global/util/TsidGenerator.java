package com.talkhasam.artichat.global.util;

import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidCreator;

// TSID 기반 ID 생성기 헬퍼 클래스 (Thread-safe)
public final class TsidGenerator {

    // private 생성자로 외부에서 객체 생성 차단
    private TsidGenerator() { }

    // 새로운 TSID(Long) 반환
    public static long nextLong() {
        Tsid tsid = TsidCreator.getTsid();
        return tsid.toLong();
    }

    // 새로운 TSID(String) 반환
    public static String nextString() {
        Tsid tsid = TsidCreator.getTsid();
        return tsid.toString();
    }
}