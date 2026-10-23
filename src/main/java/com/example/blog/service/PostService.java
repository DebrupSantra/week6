package com.example.blog.service;

import com.example.blog.dto.PostRequest;
import com.example.blog.dto.PostResponse;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;

    public PostService(
            PostRepository postRepository,
            CategoryRepository categoryRepository,
            CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> findAll(Long categoryId, String author, Pageable pageable) {
        Page<Post> posts;
        if (categoryId != null && author != null && !author.isBlank()) {
            posts = postRepository.findByCategoriesIdAndAuthorIgnoreCase(categoryId, author.trim(), pageable);
        } else if (categoryId != null) {
            posts = postRepository.findByCategoriesId(categoryId, pageable);
        } else if (author != null && !author.isBlank()) {
            posts = postRepository.findByAuthorIgnoreCase(author.trim(), pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }
        return posts.map(PostResponse::from);
    }

    @Transactional(readOnly = true)
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post " + id + " was not found"));
    }

    public Post create(PostRequest request) {
        Post post = new Post(request.title(), request.content(), request.author());
        post.setCategories(resolveCategories(request.categoryIds()));
        return postRepository.save(post);
    }

    public Post update(Long id, PostRequest request) {
        Post post = findById(id);
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setAuthor(request.author());
        post.setCategories(resolveCategories(request.categoryIds()));
        return postRepository.save(post);
    }

    public void delete(Long id) {
        Post post = findById(id);
        commentRepository.deleteByPostId(id);
        postRepository.delete(post);
    }

    private Set<Category> resolveCategories(Set<Long> categoryIds) {
        Set<Category> categories = new HashSet<>();
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category " + categoryId + " was not found"));
            categories.add(category);
        }
        return categories;
    }
}
