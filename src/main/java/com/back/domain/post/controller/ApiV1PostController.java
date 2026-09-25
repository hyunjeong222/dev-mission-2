package com.back.domain.post.controller;

import com.back.domain.post.dto.PageResponse;
import com.back.domain.post.dto.PostRequest;
import com.back.domain.post.dto.PostResponse;
import com.back.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;

    @GetMapping
    public PageResponse<PostResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return postService.list(page, size);
    }

    @GetMapping("/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody PostRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(memberId, request));
    }

    @PutMapping("/{postId}")
    public PostResponse update(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest request
    ) {
        return postService.update(memberId, postId, request);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long memberId, @PathVariable Long postId) {
        postService.delete(memberId, postId);
        return ResponseEntity.noContent().build();
    }
}