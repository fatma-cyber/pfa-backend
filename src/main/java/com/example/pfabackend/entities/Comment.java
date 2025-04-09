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
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "TEXT")
    private String content;         // Contenu du commentaire
    
    private LocalDateTime createdAt; // Date de création du commentaire
    private LocalDateTime updatedAt; // Date de mise à jour du commentaire
    
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;              // Tâche associée au commentaire
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;            // Auteur du commentaire
    
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