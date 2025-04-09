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
public class Kanban {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    @OneToMany(mappedBy = "kanban", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColumnKanban> columns = new ArrayList<>();
    
    // Méthode utilitaire pour ajouter une colonne
    public void addColumn(ColumnKanban column) {
        columns.add(column);
        column.setKanban(this);
    }
    
    // Méthode utilitaire pour supprimer une colonne
    public void removeColumn(ColumnKanban column) {
        columns.remove(column);
        column.setKanban(null);
    }
}
