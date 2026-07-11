package com.example.miniblog.controller;

import com.example.miniblog.dto.PostRequest;
import com.example.miniblog.dto.PostResponse;
import com.example.miniblog.service.PostService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest request) {
        PostResponse created = postService.create(request);
        return ResponseEntity.created(URI.create("/api/posts/" + created.id())).body(created);
    }

    @GetMapping
    public List<PostResponse> findAll() {
        return postService.findAll();
    }

    @GetMapping("/{id}")
    public PostResponse findById(@PathVariable Long id) {
        return postService.findById(id);
    }

    @PutMapping("/{id}")
    public PostResponse update(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        return postService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
