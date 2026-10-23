package com.example.blog.controller;

import com.example.blog.dto.CommentRequest;
import com.example.blog.dto.CommentResponse;
import com.example.blog.dto.PageResponse;
import com.example.blog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/api/posts/{postId}/comments")
    public PageResponse<CommentResponse> findByPost(
            @PathVariable Long postId, @PageableDefault Pageable pageable) {
        return PageResponse.from(commentService.findByPost(postId, pageable).map(CommentResponse::from));
    }

    @PostMapping("/api/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse create(
            @PathVariable Long postId, @Valid @RequestBody CommentRequest request) {
        return CommentResponse.from(commentService.create(postId, request));
    }

    @GetMapping("/api/comments/{id}")
    public CommentResponse findById(@PathVariable Long id) {
        return CommentResponse.from(commentService.findById(id));
    }

    @PutMapping("/api/comments/{id}")
    public CommentResponse update(@PathVariable Long id, @Valid @RequestBody CommentRequest request) {
        return CommentResponse.from(commentService.update(id, request));
    }

    @PatchMapping("/api/comments/{id}/moderation")
    public CommentResponse moderate(@PathVariable Long id, @Valid @RequestBody ModerationRequest request) {
        return CommentResponse.from(commentService.moderate(id, request.approved()));
    }

    @DeleteMapping("/api/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }

    public record ModerationRequest(@jakarta.validation.constraints.NotNull Boolean approved) {
    }
}
