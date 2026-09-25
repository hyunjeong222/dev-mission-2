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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private static final int MAX_PAGE_SIZE = 50;

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberService memberService;

    // 목록: 글 목록(+작성자) 쿼리 1번 + 댓글 수 집계 쿼리 1번, 총 2번 고정 (N+1 아님)
    public PageResponse<PostResponse> list(int page, int size) {
        var pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "id"));

        Page<Post> posts = postRepository.findAll(pageable);

        List<Long> postIds = posts.getContent().stream().map(Post::getId).toList();
        Map<Long, Long> commentCountByPostId = commentRepository.countByPostIdIn(postIds).stream()
                .collect(Collectors.toMap(
                        CommentRepository.PostCommentCount::getPostId,
                        CommentRepository.PostCommentCount::getCnt));

        return PageResponse.of(posts,
                p -> PostResponse.of(p, commentCountByPostId.getOrDefault(p.getId(), 0L)));
    }

    public PostResponse get(Long postId) {
        Post post = findPost(postId);
        return PostResponse.of(post, commentRepository.countByPostId(postId));
    }

    @Transactional
    public PostResponse create(Long memberId, PostRequest req) {
        var author = memberService.getAuthenticated(memberId);
        Post post = postRepository.save(new Post(author, req.title().trim(), req.content()));
        return PostResponse.of(post, 0L); // 방금 작성한 글이라 댓글이 있을 수 없음
    }

    @Transactional
    public PostResponse update(Long memberId, Long postId, PostRequest req) {
        Post post = findPost(postId);
        checkOwner(post, memberId);
        post.update(req.title().trim(), req.content());
        return PostResponse.of(post, commentRepository.countByPostId(postId));
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