package com.example.blog.service;

import com.example.blog.dto.PostRequest;
import com.example.blog.dto.CategoryRequest;
import com.example.blog.entity.Category;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.exception.ConflictException;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ServiceUnitTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void categoryCreationRejectsDuplicateNames() {
        when(categoryRepository.existsByNameIgnoreCase("Technology")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> categoryService.create(new CategoryRequest("Technology", null)));
    }

    @Test
    void postCreationResolvesRequestedCategories() {
        Category category = new Category("Technology", null);
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Post post = new PostService(postRepository, categoryRepository, commentRepository)
                .create(new PostRequest("Title", "Content", "Author", Set.of(5L)));

        assertEquals(1, post.getCategories().size());
        assertTrue(post.getCategories().contains(category));
    }

    @Test
    void commentModerationUpdatesApprovalState() {
        Comment comment = new Comment("Content", "Author", new Post("Title", "Content", "Author"));
        when(commentRepository.findById(7L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        PostService postService = new PostService(postRepository, categoryRepository, commentRepository);
        Comment moderated = new CommentService(commentRepository, postService).moderate(7L, true);

        assertTrue(moderated.isApproved());
        verify(commentRepository).save(comment);
    }
}
