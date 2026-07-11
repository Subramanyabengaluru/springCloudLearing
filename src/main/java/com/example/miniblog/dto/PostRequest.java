package com.example.miniblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequest(
        @NotBlank(message = "title is required")
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        @NotBlank(message = "content is required")
        String content,

        @Size(max = 100, message = "author must be at most 100 characters")
        String author
) {
}
