package com.example.pfabackend.controllers;

import com.example.pfabackend.dto.KanbanRequest;
import com.example.pfabackend.dto.KanbanResponse;
import com.example.pfabackend.entities.Kanban;
import com.example.pfabackend.security.jwt.UserDetailsImpl;
import com.example.pfabackend.services.KanbanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/kanbans")
public class KanbanController {

    @Autowired
    private KanbanService kanbanService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<KanbanResponse>> getAllKanbans(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Kanban> kanbans = kanbanService.getAllKanbansByUser(userDetails);
        List<KanbanResponse> response = kanbans.stream()
                .map(KanbanResponse::fromKanban)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KanbanResponse> getKanbanById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Kanban kanban = kanbanService.getKanbanById(id, userDetails);
        return ResponseEntity.ok(KanbanResponse.fromKanban(kanban));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KanbanResponse> createKanban(
            @RequestBody KanbanRequest kanbanRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Kanban kanban = new Kanban();
        kanban.setName(kanbanRequest.getName());
        kanban.setDescription(kanbanRequest.getDescription());
        kanban.setStartDate(kanbanRequest.getStartDate());
        kanban.setEndDate(kanbanRequest.getEndDate());
        
        Kanban savedKanban = kanbanService.createKanban(kanban, userDetails);
        return ResponseEntity.ok(KanbanResponse.fromKanban(savedKanban));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KanbanResponse> updateKanban(
            @PathVariable Long id,
            @RequestBody KanbanRequest kanbanRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Kanban kanban = new Kanban();
        kanban.setName(kanbanRequest.getName());
        kanban.setDescription(kanbanRequest.getDescription());
        kanban.setStartDate(kanbanRequest.getStartDate());
        kanban.setEndDate(kanbanRequest.getEndDate());
        
        Kanban updatedKanban = kanbanService.updateKanban(id, kanban, userDetails);
        return ResponseEntity.ok(KanbanResponse.fromKanban(updatedKanban));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteKanban(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        kanbanService.deleteKanban(id, userDetails);
        return ResponseEntity.ok().body("{\"message\": \"Kanban supprimé avec succès\"}");
    }
    
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<KanbanResponse>> searchKanbans(
            @RequestParam String name,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<Kanban> kanbans = kanbanService.searchKanbans(name, userDetails);
        List<KanbanResponse> response = kanbans.stream()
                .map(KanbanResponse::fromKanban)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}