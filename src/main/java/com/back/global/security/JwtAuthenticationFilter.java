package com.back.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String PREFIX = "Bearer ";
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(PREFIX)) {
            jwtProvider.parseMemberId(header.substring(PREFIX.length()).trim())
                    .ifPresent(memberId -> {
                        // principal = 회원 id(Long). 컨트롤러에서 @AuthenticationPrincipal Long 으로 받기
                        var auth = new UsernamePasswordAuthenticationToken(memberId, null, List.of());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });
        }
        // 토큰이 없거나 잘못됐으면 인증 없이 통과 → 보호된 주소면 EntryPoint 가 401 을 응답
        chain.doFilter(request, response);
    }
}
