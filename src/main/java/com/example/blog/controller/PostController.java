package com.example.blog.controller;

import com.example.blog.dto.PostRequest;
import com.example.blog.dto.PostResponse;
import com.example.blog.dto.PageResponse;
import com.example.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public PageResponse<PostResponse> findAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String author,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return PageResponse.from(postService.findAll(categoryId, author, pageable));
    }

    @GetMapping("/{id}")
    public PostResponse findById(@PathVariable Long id) {
        return PostResponse.from(postService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(@Valid @RequestBody PostRequest request) {
        return PostResponse.from(postService.create(request));
    }

    @PutMapping("/{id}")
    public PostResponse update(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        return PostResponse.from(postService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }
}
