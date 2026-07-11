package com.example.commentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotNull(message = "postId is required")
        Long postId,

        @NotBlank(message = "content is required")
        String content,

        @Size(max = 100, message = "author must be at most 100 characters")
        String author
) {
}
