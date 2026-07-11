package com.example.postservice.service;

import com.example.postservice.dto.PostRequest;
import com.example.postservice.dto.PostResponse;
import com.example.postservice.entity.Post;
import com.example.postservice.exception.ResourceNotFoundException;
import com.example.postservice.repository.PostRepository;
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

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return postRepository.existsById(id);
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
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
