package com.back.domain.member.dto;

import com.back.entity.Member;

import java.time.LocalDateTime;

// 비밀번호(해시 포함)는 응답에 절대 넣지 않습니다.
public record MemberResponse(Long id, String email, String nickname, LocalDateTime createDate) {
    public static MemberResponse from(Member m) {
        return new MemberResponse(m.getId(), m.getEmail(), m.getNickname(), m.getCreateDate());
    }
}