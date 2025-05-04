package com.example.pfabackend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.pfabackend.entities.Comment;
import com.example.pfabackend.entities.Kanban;
import com.example.pfabackend.entities.Task;
import com.example.pfabackend.entities.User;
import com.example.pfabackend.exceptions.ResourceNotFoundException;
import com.example.pfabackend.repositories.CommentRepository;
import com.example.pfabackend.repositories.KanbanRepository;
import com.example.pfabackend.repositories.TaskRepository;
import com.example.pfabackend.repositories.UserRepository;

@Service
public class CommentService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    /**
     * Récupère un commentaire par son ID
     */
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("commentaire non trouvée avec l'ID : " + id));
    }

    /**
     * Récupère toutes les commentaires d'une tache
     */
    public List<Comment> getCommentsByTaskId(Long taskId) {
        // Vérifier si la tache existe (optionnel mais recommandé)
        taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task non trouvé avec l'ID : " + taskId));
        // Utiliser le repository pour récupérer les tâches associées
        return commentRepository.findByTaskId(taskId);
    }

    /**
     * Crée un nouveau commentaire pour une tache spécifique
     */
    @Transactional
    public Comment createComment(Long taskId, Comment commentData, String assigneeEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Kanban non trouvé avec l'ID : " + taskId));

        commentData.setTask(task); // Associer le commentaire à une tache

        // Gestion de l'assignation par email
        if (StringUtils.hasText(assigneeEmail)) {
            User assignee = userRepository.findByEmail(assigneeEmail)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Utilisateur non trouvé avec l'email : " + assigneeEmail));
            commentData.setAuthor(assignee);
        } else {
            commentData.setAuthor(null); // Désassigner si l'email est vide/null
        }

        return commentRepository.save(commentData);
    }
}
