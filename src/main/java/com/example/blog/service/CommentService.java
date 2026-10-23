package com.example.blog.service;

import com.example.blog.dto.CommentRequest;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentService(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    @Transactional(readOnly = true)
    public Page<Comment> findByPost(Long postId, Pageable pageable) {
        postService.findById(postId);
        return commentRepository.findByPostId(postId, pageable);
    }

    @Transactional(readOnly = true)
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment " + id + " was not found"));
    }

    public Comment create(Long postId, CommentRequest request) {
        Post post = postService.findById(postId);
        return commentRepository.save(new Comment(request.content(), request.author(), post));
    }

    public Comment update(Long id, CommentRequest request) {
        Comment comment = findById(id);
        comment.setContent(request.content());
        comment.setAuthor(request.author());
        return commentRepository.save(comment);
    }

    public Comment moderate(Long id, boolean approved) {
        Comment comment = findById(id);
        comment.setApproved(approved);
        return commentRepository.save(comment);
    }

    public void delete(Long id) {
        commentRepository.delete(findById(id));
    }
}
