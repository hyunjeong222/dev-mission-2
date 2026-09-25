package com.back.domain.member.controller;

import com.back.domain.member.dto.JoinRequest;
import com.back.domain.member.dto.MemberResponse;
import com.back.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> join(@Valid @RequestBody JoinRequest request) {
        var member = memberService.join(request.email(), request.password(), request.nickname());
        return ResponseEntity.status(HttpStatus.CREATED).body(MemberResponse.from(member));
    }

    @GetMapping("/me")
    public MemberResponse me(@AuthenticationPrincipal Long memberId) {
        return MemberResponse.from(memberService.getAuthenticated(memberId));
    }
}