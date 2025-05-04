package com.example.pfabackend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pfabackend.entities.Comment;
import com.example.pfabackend.entities.Task;
import com.example.pfabackend.exceptions.ResourceNotFoundException;
import com.example.pfabackend.services.CommentService;
import com.example.pfabackend.services.TaskService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/task/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createComment(
            @PathVariable Long taskId,
            @RequestBody Comment comment,
            @RequestParam(required = false) String assigneeEmail) {
        try {
            Comment createdComment = commentService.createComment(taskId, comment, assigneeEmail);
            return ResponseEntity.ok(createdComment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne lors de la création du commentaire."));
        }
    }

    @GetMapping("/task/{taskId}")
    @PreAuthorize("isAuthenticated()") // Autoriser tous les utilisateurs authentifiés
    public ResponseEntity<List<Comment>> getCommentsByTaskId(@PathVariable Long taskId) {
        List<Comment> comments = commentService.getCommentsByTaskId(taskId);
        return ResponseEntity.ok(comments);
    }
}
