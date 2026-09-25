package com.back.domain.comment.service;

import com.back.domain.comment.dto.CommentRequest;
import com.back.domain.comment.dto.CommentResponse;
import com.back.domain.comment.entity.Comment;
import com.back.domain.comment.repository.CommentRepository;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.entity.Post;
import com.back.domain.post.repository.PostRepository;
import com.back.global.exception.DomainException;
import com.back.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;

    public List<CommentResponse> list(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new DomainException(ErrorCode.POST_NOT_FOUND);
        }
        return commentRepository.findByPostIdOrderByIdAsc(postId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public CommentResponse create(Long memberId, Long postId, CommentRequest req) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DomainException(ErrorCode.POST_NOT_FOUND));
        var author = memberService.getAuthenticated(memberId);
        return CommentResponse.from(commentRepository.save(new Comment(post, author, req.content())));
    }

    @Transactional
    public CommentResponse update(Long memberId, Long postId, Long commentId, CommentRequest req) {
        Comment comment = findComment(postId, commentId);
        checkOwner(comment, memberId);
        comment.update(req.content());
        return CommentResponse.from(comment);
    }

    @Transactional
    public void delete(Long memberId, Long postId, Long commentId) {
        Comment comment = findComment(postId, commentId);
        checkOwner(comment, memberId);
        commentRepository.delete(comment);
    }

    private Comment findComment(Long postId, Long commentId) {
        return commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new DomainException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void checkOwner(Comment comment, Long memberId) {
        if (!comment.isWrittenBy(memberId)) {
            throw new DomainException(ErrorCode.FORBIDDEN);
        }
    }
}