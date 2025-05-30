package com.example.pfabackend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Nom du document
    private String filePath; // Chemin vers le fichier
    private String fileType; // Type MIME du fichier
    private Long fileSize; // Taille du fichier en octets

    public Document(Long id, String name, String fileType) {
        this.id = id;
        this.name = name;
        this.fileType = fileType;
    }

    @Column(columnDefinition = "TEXT")
    private String description; // Description du document

    private LocalDateTime uploadedAt; // Date d'upload

    @ManyToOne
    @JoinColumn(name = "task_id")
    @JsonBackReference
    private Task task; // Tâche associée au document

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User uploader; // Utilisateur qui a uploadé le document

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}