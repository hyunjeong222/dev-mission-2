package com.back.entity;

import com.back.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseIdAndTime {
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false)
    private String password; // BCrypt 해시
    @Column(nullable = false, length = 30)
    private String nickname;

    public Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}