package com.example.miniblog.dto;

import java.time.Instant;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        String author,
        Instant createdAt,
        Instant updatedAt,
        List<CommentResponse> comments
) {
}
