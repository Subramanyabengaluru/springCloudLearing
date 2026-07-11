package com.example.miniblog.dto;

import java.time.Instant;

public record CommentResponse(
        Long id,
        String content,
        String author,
        Instant createdAt
) {
}
