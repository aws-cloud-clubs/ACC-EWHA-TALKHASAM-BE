package com.talkhasam.artichat.global.common;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResult<T> {
    private final List<T> items;
    private final Long nextKey;

    public PageResult(List<T> items, Long nextKey) {
        this.items = items;
        this.nextKey = nextKey;
    }
}
