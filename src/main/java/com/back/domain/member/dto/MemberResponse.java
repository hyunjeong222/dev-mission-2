package com.back.domain.member.dto;

import com.back.domain.member.entity.Member;

import java.time.LocalDateTime;

// 비밀번호는 원문을 저장하거나 응답에 내보내면 안됨
public record MemberResponse(Long id, String email, String nickname, LocalDateTime createDate) {
    public static MemberResponse from(Member m) {
        return new MemberResponse(m.getId(), m.getEmail(), m.getNickname(), m.getCreateDate());
    }
}