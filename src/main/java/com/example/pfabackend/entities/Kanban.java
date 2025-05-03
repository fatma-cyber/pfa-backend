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

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Kanban {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    private LocalDate startDate;    // Date de début du projet/kanban
    private LocalDate endDate;      // Date de fin prévue
    
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;           // Utilisateur qui a créé le kanban
    
    @JsonIgnore
    @OneToMany(mappedBy = "kanban", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
    
    // Méthode utilitaire pour ajouter une colonne
   
    
    public void addTask(Task task) {
        tasks.add(task);
        task.setKanban(this);
    }
    public void removeTask(Task task) {
        tasks.remove(task);
        task.setKanban(null);
    }
}
