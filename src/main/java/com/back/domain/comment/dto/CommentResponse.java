package com.back.domain.comment.dto;

import com.back.domain.comment.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long postId,
        String content,
        Long authorId,
        String authorNickname,
        LocalDateTime createDate,
        LocalDateTime modifyDate
) {
    public static CommentResponse from(Comment c) {
        return new CommentResponse(c.getId(), c.getPost().getId(), c.getContent(),
                c.getAuthor().getId(), c.getAuthor().getNickname(),
                c.getCreateDate(), c.getModifyDate());
    }
}