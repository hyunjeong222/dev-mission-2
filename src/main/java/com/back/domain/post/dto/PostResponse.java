package com.back.domain.post.dto;

import com.back.domain.post.entity.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        String authorNickname,
        long commentCount,
        LocalDateTime createDate,
        LocalDateTime modifyDate
) {
    public static PostResponse of(Post p, long commentCount) {
        return new PostResponse(p.getId(), p.getTitle(), p.getContent(),
                p.getAuthor().getId(), p.getAuthor().getNickname(),
                commentCount, p.getCreateDate(), p.getModifyDate());
    }
}