package com.example.pfabackend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String birthYear;
    
    @Enumerated(EnumType.STRING)
    private Role role;  // Rôle de l'utilisateur (CREATOR ou MEMBER)
    
    // Enum pour définir les rôles possibles
    public enum Role {
        CREATOR,  // Créateur du projet/kanban avec tous les droits
        MEMBER    // Membre avec des droits limités
    }
}
