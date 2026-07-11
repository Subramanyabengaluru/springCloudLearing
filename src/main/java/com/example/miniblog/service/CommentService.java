package com.example.miniblog.service;

import com.example.miniblog.dto.CommentRequest;
import com.example.miniblog.dto.CommentResponse;
import com.example.miniblog.entity.Comment;
import com.example.miniblog.entity.Post;
import com.example.miniblog.exception.ResourceNotFoundException;
import com.example.miniblog.repository.CommentRepository;
import com.example.miniblog.repository.PostRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public CommentResponse addComment(Long postId, CommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + postId));

        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setAuthor(request.author());
        comment.setPost(post);

        return toResponse(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findByPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id " + postId);
        }
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId));
        commentRepository.delete(comment);
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor(),
                comment.getCreatedAt()
        );
    }
}
