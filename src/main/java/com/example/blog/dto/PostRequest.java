package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record PostRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String content,
        @NotBlank @Size(max = 120) String author,
        @NotNull Set<@NotNull Long> categoryIds
) {
}
