package com.back.domain.member.service;

import com.back.domain.member.repository.MemberRepository;
import com.back.entity.Member;
import com.back.global.exception.DomainException;
import com.back.global.exception.ErrorCode;
import com.back.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public String login(String email, String password) {
        // 이메일이 없는 경우와 비밀번호가 틀린 경우를 같은 응답으로 처리해 가입 여부가 드러나지 않게 함
        Member member = memberRepository.findByEmail(MemberService.normalize(email))
                .filter(m -> passwordEncoder.matches(password, m.getPassword()))
                .orElseThrow(() -> new DomainException(ErrorCode.INVALID_CREDENTIALS));

        return jwtProvider.createAccessToken(member.getId());
    }
}