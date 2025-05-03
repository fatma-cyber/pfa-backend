package com.example.pfabackend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class KanbanRequest {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}