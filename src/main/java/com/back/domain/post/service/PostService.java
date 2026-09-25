package com.back.domain.post.service;

import com.back.domain.comment.repository.CommentRepository;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.dto.PageResponse;
import com.back.domain.post.dto.PostRequest;
import com.back.domain.post.dto.PostResponse;
import com.back.domain.post.entity.Post;
import com.back.domain.post.repository.PostRepository;
import com.back.global.exception.DomainException;
import com.back.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private static final int MAX_PAGE_SIZE = 50;

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberService memberService;

    public PageResponse<PostResponse> list(int page, int size) {
        var pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "id"));
        return PageResponse.of(postRepository.findAll(pageable), PostResponse::from);
    }

    public PostResponse get(Long postId) {
        return PostResponse.from(findPost(postId));
    }

    @Transactional
    public PostResponse create(Long memberId, PostRequest req) {
        var author = memberService.getAuthenticated(memberId);
        Post post = postRepository.save(new Post(author, req.title().trim(), req.content()));
        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse update(Long memberId, Long postId, PostRequest req) {
        Post post = findPost(postId);
        checkOwner(post, memberId);
        post.update(req.title().trim(), req.content());
        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long memberId, Long postId) {
        Post post = findPost(postId);
        checkOwner(post, memberId);
        commentRepository.deleteByPostId(postId); // 댓글 먼저 지워야 FK 오류(500)가 나지 않음
        postRepository.delete(post);
    }

    private Post findPost(Long postId) {
        return postRepository.findWithAuthorById(postId)
                .orElseThrow(() -> new DomainException(ErrorCode.POST_NOT_FOUND));
    }

    private void checkOwner(Post post, Long memberId) {
        if (!post.isWrittenBy(memberId)) {
            throw new DomainException(ErrorCode.FORBIDDEN);
        }
    }
}