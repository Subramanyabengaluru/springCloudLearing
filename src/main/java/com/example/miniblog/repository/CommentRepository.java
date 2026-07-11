package com.example.miniblog.repository;

import com.example.miniblog.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
