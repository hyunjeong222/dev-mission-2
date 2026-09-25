package com.back.global.security;

import com.back.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// 필터 단계의 오류는 @RestControllerAdvice 가 잡지 못하므로, 같은 모양의 JSON 을 직접 사용
final class SecurityErrorWriter {
    private SecurityErrorWriter() {}

    static void write(HttpServletResponse response, ErrorCode code) throws IOException {
        response.setStatus(code.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"status\":%d,\"code\":\"%s\",\"message\":\"%s\",\"errors\":[]}"
                        .formatted(code.getStatus().value(), code.name(), code.getMessage())
        );
    }
}