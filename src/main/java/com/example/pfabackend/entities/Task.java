package com.example.pfabackend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // Titre de la tâche
    private String description; // Description détaillée

    @Enumerated(EnumType.STRING)
    private Status status; // Statut de la tâche

    @Enumerated(EnumType.STRING)
    private Priority priority; // Priorité de la tâche

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee; // Utilisateur assigné à cette tâche

    private LocalDate deadline; // Date limite
    private String color; // Couleur de la carte pour l'affichage
    private Integer storyPoints; // Points d'histoire (pour l'estimation agile)

    // @JsonIgnore

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    // @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "kanban_id")
    private Kanban kanban; // Utilisateur assigné à cette tâche

    // Méthodes utilitaires pour les commentaires
    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setTask(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setTask(null);
    }

    // Méthodes utilitaires pour les documents
    public void addDocument(Document document) {
        documents.add(document);
        document.setTask(this);
    }

    public void removeDocument(Document document) {
        documents.remove(document);
        document.setTask(null);
    }

    // Enums pour statut et priorité
    public enum Status {
        TODO("À faire"),
        IN_PROGRESS("En cours"),

        DONE("Terminé");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Priority {
        LOW("Basse"),
        MEDIUM("Moyenne"),
        HIGH("Haute"),
        URGENT("Urgente");

        private final String displayName;

        Priority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}