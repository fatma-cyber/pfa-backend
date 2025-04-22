package com.example.pfabackend.controllers;

import com.example.pfabackend.entities.Task;
import com.example.pfabackend.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }
    
    @PostMapping("/kanban/{kanbanId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> createTask(
            @PathVariable Long kanbanId,
            @RequestBody Task task) {
        Task createdTask = taskService.createTask(kanbanId, task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody Task taskDetails) {
        Task updatedTask = taskService.updateTask(id, taskDetails);
        return ResponseEntity.ok(updatedTask);
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest statusRequest) {
        Task updatedTask = taskService.updateTaskStatus(id, statusRequest.getStatus());
        return ResponseEntity.ok(updatedTask);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().body("{\"message\": \"Tâche supprimée avec succès\"}");
    }
    
    @GetMapping("/kanban/{kanbanId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Task>> getTasksByKanbanId(@PathVariable Long kanbanId) {
        List<Task> tasks = taskService.getTasksByKanbanId(kanbanId);
        return ResponseEntity.ok(tasks);
    }
    
    // Classe utilisée pour les requêtes de mise à jour de statut
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