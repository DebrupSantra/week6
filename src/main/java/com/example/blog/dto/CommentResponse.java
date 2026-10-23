package com.example.blog.dto;

import com.example.blog.entity.Comment;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long postId,
        String content,
        String author,
        boolean approved,
        Instant createdAt
) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getContent(),
                comment.getAuthor(),
                comment.isApproved(),
                comment.getCreatedAt()
        );
    }
}
