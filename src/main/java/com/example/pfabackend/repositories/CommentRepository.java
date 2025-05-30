package com.example.pfabackend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pfabackend.entities.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskId(Long taskId);

}
