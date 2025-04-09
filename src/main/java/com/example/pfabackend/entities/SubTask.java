package com.example.pfabackend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;             // Titre de la sous-tâche
    private String description;       // Description détaillée
    
    @Enumerated(EnumType.STRING)
    private Status status;            // État de la sous-tâche (À faire, En cours, Terminée)
    
    private Integer estimatedMinutes; // Temps estimé en minutes
    private Integer actualMinutes;    // Temps réellement passé
    
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task parentTask;          // Référence à la tâche/user story parente
    
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;            // Personne assignée à cette sous-tâche
    
    private LocalDateTime createdAt;  // Date de création
    private LocalDateTime updatedAt;  // Date de dernière modification
    
    // Enum pour le statut
    public enum Status {
        TODO("À faire"),
        IN_PROGRESS("En cours"),
        DONE("Terminée");
        
        private final String displayName;
        
        Status(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Méthodes pour gérer les dates automatiquement
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}