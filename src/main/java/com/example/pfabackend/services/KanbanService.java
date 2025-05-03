package com.example.pfabackend.services;

import com.example.pfabackend.entities.Kanban;
import com.example.pfabackend.entities.Task;
import com.example.pfabackend.entities.User;
import com.example.pfabackend.exceptions.ForbiddenException;
import com.example.pfabackend.exceptions.ResourceNotFoundException;
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
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

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
     * Met à jour un kanban existant (accès étendu)
     */
    @Transactional
    public Kanban updateKanban(Long id, Kanban kanbanDetails, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Kanban kanban = kanbanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kanban non trouvé avec l'ID : " + id));
        
        // Vérifier si l'utilisateur a accès (créateur ou assigné)
        if (!userHasAccessToKanban(kanban, user)) {
            throw new ForbiddenException("Vous n'avez pas accès à ce kanban");
        }
        
        // Mise à jour des champs
        kanban.setName(kanbanDetails.getName());
        kanban.setDescription(kanbanDetails.getDescription());
        kanban.setStartDate(kanbanDetails.getStartDate());
        kanban.setEndDate(kanbanDetails.getEndDate());
        
        return kanbanRepository.save(kanban);
    }

    /**
     * Supprime un kanban (accès étendu)
     */
    @Transactional
    public void deleteKanban(Long id, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Kanban kanban = kanbanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kanban non trouvé avec l'ID : " + id));
        
        // Vérifier si l'utilisateur a accès (créateur ou assigné)
        if (!userHasAccessToKanban(kanban, user)) {
            throw new ForbiddenException("Vous n'avez pas accès à ce kanban");
        }
        
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

    /**
     * Récupère tous les projets où l'utilisateur est impliqué (soit comme créateur, soit comme assigné à des tâches)
     */
    public List<Kanban> getProjectsInvolving(Long userId) {
        // Récupère l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));
        
        // Récupère tous les projets créés par l'utilisateur
        List<Kanban> createdProjects = kanbanRepository.findByCreator(user);
        
        // Récupère tous les projets où l'utilisateur est assigné à au moins une tâche
        List<Kanban> assignedProjects = kanbanRepository.findDistinctByTasks_Assignee(user);
        
        // Fusionne les deux listes en évitant les doublons
        Set<Kanban> allProjects = new HashSet<>();
        allProjects.addAll(createdProjects);
        allProjects.addAll(assignedProjects);
        
        return new ArrayList<>(allProjects);
    }

    /**
     * Vérifie si un utilisateur a accès à un kanban (créateur ou assigné à une tâche)
     * @return true si l'utilisateur a accès, false sinon
     */
    private boolean userHasAccessToKanban(Kanban kanban, User user) {
        // L'utilisateur est le créateur du kanban
        if (kanban.getCreator() != null && kanban.getCreator().getId().equals(user.getId())) {
            return true;
        }
        
        // L'utilisateur est assigné à au moins une tâche du kanban
        return kanban.getTasks().stream()
                .anyMatch(task -> task.getAssignee() != null && 
                        task.getAssignee().getId().equals(user.getId()));
    }

    /**
     * Récupère un kanban par son ID avec une vérification d'accès étendue (créateur ou assigné)
     */
    public Kanban getKanbanByIdWithExtendedAccess(Long id, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Kanban kanban = kanbanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kanban non trouvé avec l'ID : " + id));
        
        if (!userHasAccessToKanban(kanban, user)) {
            throw new ForbiddenException("Vous n'avez pas accès à ce kanban");
        }
        
        return kanban;
    }
}