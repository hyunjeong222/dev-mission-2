package com.back.domain.member.service;

import com.back.entity.Member;
import com.back.global.exception.DomainException;
import com.back.global.exception.ErrorCode;
import com.back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member join(String email, String password, String nickname) {
        String normalizedEmail = normalize(email);

        if (memberRepository.existsByEmail(normalizedEmail)) {
            throw new DomainException(ErrorCode.DUPLICATE_EMAIL);
        }

        try {
            return memberRepository.saveAndFlush(
                    new Member(normalizedEmail, passwordEncoder.encode(password), nickname.trim())
            );
        } catch (DataIntegrityViolationException e) {
            throw new DomainException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    /** 토큰의 회원 id 로 현재 회원을 찾습니다. 없으면 인증이 무효한 것이므로 401. */
    public Member getAuthenticated(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new DomainException(ErrorCode.UNAUTHORIZED));
    }

    public static String normalize(String email) {
        return email.trim().toLowerCase();
    }
}