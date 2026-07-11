package com.example.miniblog.controller;

import com.example.miniblog.dto.CommentRequest;
import com.example.miniblog.dto.CommentResponse;
import com.example.miniblog.service.CommentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse created = commentService.addComment(postId, request);
        return ResponseEntity
                .created(URI.create("/api/comments/" + created.id()))
                .body(created);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentResponse> findByPost(@PathVariable Long postId) {
        return commentService.findByPost(postId);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
