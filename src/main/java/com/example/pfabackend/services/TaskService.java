package com.example.pfabackend.services;

import com.example.pfabackend.entities.Comment;
import com.example.pfabackend.entities.Kanban;
import com.example.pfabackend.entities.Task;
import com.example.pfabackend.entities.User;
import com.example.pfabackend.exceptions.ResourceNotFoundException;
import com.example.pfabackend.repositories.CommentRepository;
import com.example.pfabackend.repositories.KanbanRepository;
import com.example.pfabackend.repositories.TaskRepository;
import com.example.pfabackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private KanbanRepository kanbanRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    /**
     * Récupère toutes les tâches
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Récupère une tâche par son ID
     */
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée avec l'ID : " + id));
    }

    /**
     * Récupère toutes les tâches d'un kanban
     */
    public List<Task> getTasksByKanbanId(Long kanbanId) {
        // Vérifier si le Kanban existe (optionnel mais recommandé)
        kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new ResourceNotFoundException("Kanban non trouvé avec l'ID : " + kanbanId));
        // Utiliser le repository pour récupérer les tâches associées
        return taskRepository.findByKanbanId(kanbanId);
    }

    /**
     * Crée une nouvelle tâche pour un kanban spécifique
     */
    @Transactional
    public Task createTask(Long kanbanId, Task taskData, String assigneeEmail) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new ResourceNotFoundException("Kanban non trouvé avec l'ID : " + kanbanId));

        taskData.setKanban(kanban); // Associer la tâche au Kanban

        // Gestion de l'assignation par email
        if (StringUtils.hasText(assigneeEmail)) {
            User assignee = userRepository.findByEmail(assigneeEmail)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Utilisateur non trouvé avec l'email : " + assigneeEmail));
            taskData.setAssignee(assignee);
        } else {
            taskData.setAssignee(null); // Désassigner si l'email est vide/null
        }

        // Valeurs par défaut pour les enums si non fournies
        if (taskData.getStatus() == null) {
            taskData.setStatus(Task.Status.TODO);
        }
        if (taskData.getPriority() == null) {
            taskData.setPriority(Task.Priority.MEDIUM);
        }

        // Long idSavedTask = tasckSaved.getId();
        Task taskSaved = taskRepository.save(taskData);
        List<Comment> comments = new ArrayList<>(taskSaved.getComments());

        for (Comment comment : comments) {
            // taskSaved.addComment(comment);
            comment.setTask(taskSaved);
            commentRepository.save(comment);
        }

        return taskSaved;
        // return taskRepository.save(taskData);
    }

    /**
     * Met à jour une tâche existante
     */
    @Transactional
    public Task updateTask(Long id, Task taskDetails, String assigneeEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée avec l'ID : " + id));

        // Mise à jour des champs (sauf l'ID et le Kanban qui ne doivent pas changer
        // ici)
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        // S'assurer que les enums ne sont pas null avant de les mettre à jour
        if (taskDetails.getStatus() != null) {
            task.setStatus(taskDetails.getStatus());
        }
        if (taskDetails.getPriority() != null) {
            task.setPriority(taskDetails.getPriority());
        }
        task.setDeadline(taskDetails.getDeadline());
        task.setColor(taskDetails.getColor());
        task.setStoryPoints(taskDetails.getStoryPoints());

        // Gestion de l'assignation par email
        if (StringUtils.hasText(assigneeEmail)) {
            User assignee = userRepository.findByEmail(assigneeEmail)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Utilisateur non trouvé avec l'email : " + assigneeEmail));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null); // Désassigner si l'email est vide/null
        }

        // Task taskUpdated = taskRepository.save(task);
        List<Comment> comments = new ArrayList<>(taskDetails.getComments());
        // System.out.println("comment******************* 1: " +
        // taskDetails.getComments());
        for (Comment comment : comments) {
            // taskSaved.addComment(comment);
            comment.setTask(task);
            commentRepository.save(comment);
            System.out.println("comment*******************2 : " + comment.getContent());
        }

        // return taskUpdated;

        return taskRepository.save(task);
    }

    /**
     * Change le statut d'une tâche
     */
    @Transactional
    public Task updateTaskStatus(Long id, Task.Status status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée avec l'ID : " + id));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    /**
     * Supprime une tâche
     */
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée avec l'ID : " + id));
        taskRepository.delete(task);
    }
}