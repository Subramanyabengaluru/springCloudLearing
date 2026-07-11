package com.example.postservice.dto;

import java.time.Instant;

public record PostResponse(
        Long id,
        String title,
        String content,
        String author,
        Instant createdAt,
        Instant updatedAt
) {
}
