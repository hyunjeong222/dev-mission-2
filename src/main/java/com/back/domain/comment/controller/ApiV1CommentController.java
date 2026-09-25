package com.back.domain.comment.controller;

import com.back.domain.comment.dto.CommentRequest;
import com.back.domain.comment.dto.CommentResponse;
import com.back.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class ApiV1CommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentResponse> list(@PathVariable Long postId) {
        return commentService.list(postId);
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(memberId, postId, request));
    }

    @PutMapping("/{commentId}")
    public CommentResponse update(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request
    ) {
        return commentService.update(memberId, postId, commentId, request);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.delete(memberId, postId, commentId);
        return ResponseEntity.noContent().build();
    }
}