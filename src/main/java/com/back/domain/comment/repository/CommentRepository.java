package com.back.domain.comment.repository;

import com.back.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = "author")
    List<Comment> findByPostIdOrderByIdAsc(Long postId);

    // 댓글 id 가 실제로 그 글의 댓글인지까지 함께 확인
    @EntityGraph(attributePaths = "author")
    Optional<Comment> findByIdAndPostId(Long id, Long postId);

    @Modifying
    @Query("delete from Comment c where c.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    // 글 목록 조회 시, 글 개수와 무관하게 댓글 수 집계를 쿼리 1번으로 끝내기 위한 메서드
    @Query("select c.post.id as postId, count(c) as cnt from Comment c where c.post.id in :postIds group by c.post.id")
    List<PostCommentCount> countByPostIdIn(@Param("postIds") List<Long> postIds);

    // 글 상세 조회처럼 글이 1건일 때는 IN 절 없이 단건 count 로 충분
    long countByPostId(Long postId);

    interface PostCommentCount {
        Long getPostId();
        Long getCnt();
    }
}
