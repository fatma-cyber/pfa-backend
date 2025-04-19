package com.example.pfabackend.dto;

import com.example.pfabackend.entities.Kanban;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class KanbanResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String creatorUsername;
    private List<TaskSummaryDTO> tasks;
    
    public static KanbanResponse fromKanban(Kanban kanban) {
        KanbanResponse response = new KanbanResponse();
        response.setId(kanban.getId());
        response.setName(kanban.getName());
        response.setDescription(kanban.getDescription());
        response.setStartDate(kanban.getStartDate());
        response.setEndDate(kanban.getEndDate());
        response.setCreatorUsername(kanban.getCreator().getUsername());
        
        // Convertir les tâches en DTO simplifiés
        if (kanban.getTasks() != null) {
            response.setTasks(kanban.getTasks().stream()
                .map(task -> {
                    TaskSummaryDTO dto = new TaskSummaryDTO();
                    dto.setId(task.getId());
                    dto.setTitle(task.getTitle());
                    dto.setStatus(task.getStatus().toString());
                    return dto;
                })
                .collect(Collectors.toList()));
        }
        
        return response;
    }
    
    @Data
    public static class TaskSummaryDTO {
        private Long id;
        private String title;
        private String status;
    }
}