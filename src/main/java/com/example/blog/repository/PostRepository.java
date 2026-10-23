package com.example.blog.repository;

import com.example.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    @EntityGraph(attributePaths = "categories")
    Optional<Post> findById(Long id);

    @Override
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByCategoriesId(Long categoryId, Pageable pageable);

    Page<Post> findByAuthorIgnoreCase(String author, Pageable pageable);

    Page<Post> findByCategoriesIdAndAuthorIgnoreCase(Long categoryId, String author, Pageable pageable);
}
