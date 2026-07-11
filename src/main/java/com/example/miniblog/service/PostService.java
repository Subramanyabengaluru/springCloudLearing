package com.example.miniblog.service;

import com.example.miniblog.dto.CommentResponse;
import com.example.miniblog.dto.PostRequest;
import com.example.miniblog.dto.PostResponse;
import com.example.miniblog.entity.Post;
import com.example.miniblog.exception.ResourceNotFoundException;
import com.example.miniblog.repository.PostRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponse create(PostRequest request) {
        Post post = new Post();
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setAuthor(request.author());
        return toResponse(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostResponse findById(Long id) {
        return toResponse(getPostOrThrow(id));
    }

    @Transactional
    public PostResponse update(Long id, PostRequest request) {
        Post post = getPostOrThrow(id);
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setAuthor(request.author());
        return toResponse(postRepository.save(post));
    }

    @Transactional
    public void delete(Long id) {
        Post post = getPostOrThrow(id);
        postRepository.delete(post);
    }

    private Post getPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + id));
    }

    private PostResponse toResponse(Post post) {
        List<CommentResponse> comments = post.getComments().stream()
                .map(c -> new CommentResponse(c.getId(), c.getContent(), c.getAuthor(), c.getCreatedAt()))
                .toList();
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                comments
        );
    }
}
