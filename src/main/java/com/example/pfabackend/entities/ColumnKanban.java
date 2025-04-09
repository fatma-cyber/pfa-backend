package com.example.pfabackend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ColumnKanban {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;           // Titre de la colonne (ex: "À faire", "En cours", "Terminé")
    private Integer position;       // Position de la colonne dans le tableau Kanban
    private String backgroundColor; // Couleur de fond pour la colonne
    
    @ManyToOne
    @JoinColumn(name = "kanban_id")
    private Kanban kanban;          // Relation avec le tableau Kanban parent
    
    private Integer wipLimit;       // Limite de tâches en cours (Work in Progress)
    
    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>(); // Liste des tâches dans cette colonne
}