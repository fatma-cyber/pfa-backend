package com.example.pfabackend.services;

import com.example.pfabackend.entities.Kanban;
import com.example.pfabackend.entities.Task;
import com.example.pfabackend.entities.User;
import com.example.pfabackend.repositories.KanbanRepository;
import com.example.pfabackend.repositories.UserRepository;
import com.example.pfabackend.repositories.TaskRepository;  // Ajoutez cette injection
// Correction de l'import - UserDetailsImpl est dans le package security.jwt
import com.example.pfabackend.security.jwt.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class KanbanService {

    @Autowired
    private KanbanRepository kanbanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;  // Ajoutez cette injection

    /**
     * Récupère tous les kanbans d'un utilisateur
     */
    public List<Kanban> getAllKanbansByUser(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return kanbanRepository.findByCreator(user);
    }

    /**
     * Récupère un kanban par son ID (vérifie si l'utilisateur a accès)
     */
    public Kanban getKanbanById(Long id, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return kanbanRepository.findByIdAndCreator(id, user)
                .orElseThrow(() -> new RuntimeException("Kanban not found or access denied"));
    }

    /**
     * Crée un nouveau kanban
     */
    @Transactional
    public Kanban createKanban(Kanban kanban, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        kanban.setCreator(user);
        return kanbanRepository.save(kanban);
    }

    /**
     * Met à jour un kanban existant
     */
    @Transactional
    public Kanban updateKanban(Long id, Kanban kanbanDetails, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Kanban kanban = kanbanRepository.findByIdAndCreator(id, user)
                .orElseThrow(() -> new RuntimeException("Kanban not found or access denied"));
        
        // Mise à jour des champs
        kanban.setName(kanbanDetails.getName());
        kanban.setDescription(kanbanDetails.getDescription());
        kanban.setStartDate(kanbanDetails.getStartDate());
        kanban.setEndDate(kanbanDetails.getEndDate());
        
        return kanbanRepository.save(kanban);
    }

    /**
     * Supprime un kanban
     */
    @Transactional
    public void deleteKanban(Long id, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Kanban kanban = kanbanRepository.findByIdAndCreator(id, user)
                .orElseThrow(() -> new RuntimeException("Kanban not found or access denied"));
        
        kanbanRepository.delete(kanban);
    }
    
    /**
     * Recherche des kanbans par nom
     */
    public List<Kanban> searchKanbans(String name, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return kanbanRepository.findByNameContainingAndCreator(name, user);
    }

    /**
     * Récupère toutes les tâches d'un kanban spécifique
     */
    public List<Task> getTasksByKanbanId(Long kanbanId, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Kanban kanban = kanbanRepository.findByIdAndCreator(kanbanId, user)
                .orElseThrow(() -> new RuntimeException("Kanban not found or access denied"));
        
        return kanban.getTasks();
    }

    /**
     * Récupère toutes les tâches d'un kanban spécifique
     * @param kanbanId l'ID du kanban
     * @param userId l'ID de l'utilisateur pour vérifier l'accès
     * @return la liste des tâches du kanban
     */
    public List<Task> getTasksByKanbanId(Long kanbanId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Kanban kanban = kanbanRepository.findByIdAndCreator(kanbanId, user)
                .orElseThrow(() -> new RuntimeException("Kanban not found or access denied"));
        
        return kanban.getTasks();
    }

    /**
     * Récupère toutes les tâches d'un kanban spécifique sans vérification d'utilisateur
     * Note: À utiliser uniquement dans un contexte où la sécurité est gérée à un autre niveau
     */
    public List<Task> getTasksByKanbanId(Long kanbanId) {
        // Vérifiez d'abord que le kanban existe
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new RuntimeException("Kanban not found"));
        
        // Au lieu d'utiliser kanban.getTasks(), utilisez directement le repository
        return taskRepository.findByKanbanId(kanbanId);
    }

    /**
     * Crée une tâche pour un kanban spécifique
     */
    @Transactional
    public Task createTask(Long kanbanId, Task taskData) {
        Kanban kanban = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new RuntimeException("Kanban not found"));
        
        taskData.setKanban(kanban);
        kanban.getTasks().add(taskData);
        kanbanRepository.save(kanban);
        
        return taskData;
    }
}