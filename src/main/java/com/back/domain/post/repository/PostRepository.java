package com.back.domain.post.repository;

import com.back.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    // author 를 함께 JOIN 해서 가져와, 글마다 작성자를 따로 조회하는 N+1 을 막기
    @EntityGraph(attributePaths = "author")
    Page<Post> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "author")
    Optional<Post> findWithAuthorById(Long id);
}
