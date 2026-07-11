package com.example.commentservice.service;

import com.example.commentservice.client.PostClient;
import com.example.commentservice.dto.CommentRequest;
import com.example.commentservice.dto.CommentResponse;
import com.example.commentservice.entity.Comment;
import com.example.commentservice.exception.ResourceNotFoundException;
import com.example.commentservice.repository.CommentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostClient postClient;

    public CommentService(CommentRepository commentRepository, PostClient postClient) {
        this.commentRepository = commentRepository;
        this.postClient = postClient;
    }

    @Transactional
    public CommentResponse addComment(CommentRequest request) {
        // Inter-service check: the post lives in post-service / posts_db.
        if (!postClient.postExists(request.postId())) {
            throw new ResourceNotFoundException("Post not found with id " + request.postId());
        }

        Comment comment = new Comment();
        comment.setPostId(request.postId());
        comment.setContent(request.content());
        comment.setAuthor(request.author());

        return toResponse(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findByPost(Long postId) {
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
                comment.getPostId(),
                comment.getContent(),
                comment.getAuthor(),
                comment.getCreatedAt()
        );
    }
}
