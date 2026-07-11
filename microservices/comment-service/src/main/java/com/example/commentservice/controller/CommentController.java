package com.example.commentservice.controller;

import com.example.commentservice.dto.CommentRequest;
import com.example.commentservice.dto.CommentResponse;
import com.example.commentservice.service.CommentService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@Valid @RequestBody CommentRequest request) {
        CommentResponse created = commentService.addComment(request);
        return ResponseEntity
                .created(URI.create("/api/comments/" + created.id()))
                .body(created);
    }

    @GetMapping
    public List<CommentResponse> findByPost(@RequestParam Long postId) {
        return commentService.findByPost(postId);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
