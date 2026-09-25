package com.back.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record JoinRequest (
        @NotBlank(message = "이메일은 필수입니다.")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Pattern(
                regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,}$",
                message = "이메일 형식이 올바르지 않습니다."
        )
        String email,
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 10, max = 16, message = "비밀번호는 10자 이상 16자 이하여야 합니다.")
        String password,
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 30, message = "닉네임은 30자 이하여야 합니다.")
        String nickname
) {}