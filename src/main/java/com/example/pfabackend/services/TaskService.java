package com.example.pfabackend.services;

import com.example.pfabackend.entities.Kanban;
import com.example.pfabackend.entities.Task;
import com.example.pfabackend.entities.User;
import com.example.pfabackend.repositories.KanbanRepository;
import com.example.pfabackend.repositories.TaskRepository;
import com.example.pfabackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private KanbanRepository kanbanRepository;
    
    @Autowired
    private UserRepository userRepository;
    
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
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID : " + id));
    }
    
    /**
     * Crée une nouvelle tâche pour un kanban spécifique
     */
    @Transactional
    public Task createTask(Long kanbanId, Task task) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new RuntimeException("Kanban non trouvé avec l'ID : " + kanbanId));
        
        // S'assurer que la tâche est correctement liée au kanban
        task.setKanban(kanban);
        
        try {
            // Log pour débogage
            System.out.println("Création de tâche: " + task.getTitle());
            System.out.println("Status reçu: " + task.getStatus());
            System.out.println("Priority reçue: " + task.getPriority());
            System.out.println("KanbanId reçu: " + task.getKanban().getId());
            
            // Assigner l'utilisateur si fourni
            if (task.getAssignee() != null && task.getAssignee().getId() != null) {
                User assignee = userRepository.findById(task.getAssignee().getId())
                        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
                task.setAssignee(assignee);
            }
            
            // Vérifier et corriger les enums si nécessaire
            if (task.getStatus() == null) {
                task.setStatus(Task.Status.TODO);
            }
            
            if (task.getPriority() == null) {
                task.setPriority(Task.Priority.MEDIUM);
            }
            
            return taskRepository.save(task);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Met à jour une tâche existante
     */
    @Transactional
    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID : " + id));
        
        // Mise à jour des champs
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());
        task.setDeadline(taskDetails.getDeadline());
        task.setColor(taskDetails.getColor());
        task.setStoryPoints(taskDetails.getStoryPoints());
        
        // Mise à jour de l'assignee si fournie
        if (taskDetails.getAssignee() != null && taskDetails.getAssignee().getId() != null) {
            User assignee = userRepository.findById(taskDetails.getAssignee().getId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null);
        }
        
        return taskRepository.save(task);
    }
    
    /**
     * Change le statut d'une tâche
     */
    @Transactional
    public Task updateTaskStatus(Long id, Task.Status status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID : " + id));
        
        task.setStatus(status);
        return taskRepository.save(task);
    }
    
    /**
     * Supprime une tâche
     */
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID : " + id));
        
        taskRepository.delete(task);
    }
    
    /**
     * Récupère toutes les tâches d'un kanban
     */
    public List<Task> getTasksByKanbanId(Long kanbanId) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new RuntimeException("Kanban non trouvé avec l'ID : " + kanbanId));
        
        return kanban.getTasks();
    }
}