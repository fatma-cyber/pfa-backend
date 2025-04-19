package com.example.pfabackend.repositories;

import com.example.pfabackend.entities.Kanban;
import com.example.pfabackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KanbanRepository extends JpaRepository<Kanban, Long> {
    // Trouver tous les kanbans créés par un utilisateur spécifique
    List<Kanban> findByCreator(User creator);
    
    // Trouver un kanban par ID et créateur (pour s'assurer que l'utilisateur a accès)
    Optional<Kanban> findByIdAndCreator(Long id, User creator);
    
    // Rechercher des kanbans par nom (contenant le terme de recherche)
    List<Kanban> findByNameContainingAndCreator(String name, User creator);
}