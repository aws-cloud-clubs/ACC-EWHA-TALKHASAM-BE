package com.talkhasam.artichat.global.exception;

import java.util.List;

public record ErrorResponse(
        int status,
        String errorCode,
        String message,
        String path,
        String timestamp,
        List<String> details
) {}