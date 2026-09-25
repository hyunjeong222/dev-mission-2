package com.back.global.exception;

import java.util.List;

// 모든 오류 응답의 공통 모양: { status, code, message, errors[] }
public record ErrorResponse(
        int status,
        String code,
        String message,
        List<FieldErrorDetail> errors
) {
    public record FieldErrorDetail(String field, String message) {}

    public static ErrorResponse of(ErrorCode errorCode) {
        return of(errorCode, List.of());
    }

    public static ErrorResponse of(ErrorCode errorCode, List<FieldErrorDetail> errors) {
        return new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.name(),
                errorCode.getMessage(),
                errors
        );
    }
}
