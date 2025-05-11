package com.example.pfabackend.controllers;

import com.example.pfabackend.entities.Comment;
import com.example.pfabackend.entities.Document;
import com.example.pfabackend.entities.Task;
import com.example.pfabackend.exceptions.ResourceNotFoundException;
import com.example.pfabackend.repositories.DocumentRepository;
import com.example.pfabackend.services.DocumentService;
import com.example.pfabackend.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            Task task = taskService.getTaskById(id);
            return ResponseEntity.ok(task);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/kanban/{kanbanId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createTask(

            @PathVariable Long kanbanId,

            @RequestBody Task task,

            @RequestParam(required = false) String assigneeEmail) {
        try {
            Task createdTask = taskService.createTask(kanbanId, task, assigneeEmail);
            return ResponseEntity.ok(createdTask);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message",
                    e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne lors de la création de la tâche."));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateTask(
            @PathVariable Long id,
            @RequestBody Task taskDetails,
            @RequestParam(required = false) String assigneeEmail) {
        try {
            Task updatedTask = taskService.updateTask(id, taskDetails, assigneeEmail);
            return ResponseEntity.ok(updatedTask);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne lors de la mise à jour de la tâche."));
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest statusRequest) {
        try {
            if (statusRequest.getStatus() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le statut ne peut pas être null."));
            }
            Task updatedTask = taskService.updateTaskStatus(id, statusRequest.getStatus());
            return ResponseEntity.ok(updatedTask);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne lors de la mise à jour du statut."));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok().body(Map.of("message", "Tâche supprimée avec succès"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne lors de la suppression de la tâche."));
        }
    }

    @GetMapping("/kanban/{kanbanId}")
    @PreAuthorize("isAuthenticated()") // Autoriser tous les utilisateurs authentifiés
    public ResponseEntity<List<Task>> getTasksByKanbanId(@PathVariable Long kanbanId) {
        List<Task> tasks = taskService.getTasksByKanbanId(kanbanId);
        return ResponseEntity.ok(tasks);
    }

    static class StatusUpdateRequest {
        private Task.Status status;

        public Task.Status getStatus() {
            return status;
        }

        public void setStatus(Task.Status status) {
            this.status = status;
        }
    }
}