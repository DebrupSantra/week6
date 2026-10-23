package com.example.blog.dto;

import com.example.blog.entity.Post;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record PostResponse(
        Long id,
        String title,
        String content,
        String author,
        Instant createdAt,
        Instant updatedAt,
        Set<CategoryResponse> categories
) {

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getCategories().stream().map(CategoryResponse::from).collect(Collectors.toUnmodifiableSet())
        );
    }
}
