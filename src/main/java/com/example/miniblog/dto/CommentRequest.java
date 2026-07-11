package com.example.miniblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotBlank(message = "content is required")
        String content,

        @Size(max = 100, message = "author must be at most 100 characters")
        String author
) {
}
