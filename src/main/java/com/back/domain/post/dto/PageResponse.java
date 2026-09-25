package com.back.domain.post.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

// 필요한 값만 담은 응답 사용
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
    public static <E, T> PageResponse<T> of(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}