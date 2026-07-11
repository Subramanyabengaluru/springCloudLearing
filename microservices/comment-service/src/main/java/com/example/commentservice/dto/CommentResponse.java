package com.example.commentservice.dto;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long postId,
        String content,
        String author,
        Instant createdAt
) {
}
