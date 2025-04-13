package com.example.pfabackend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.pfabackend.entities.Comment;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}