package com.example.pfabackend.repositories;

import com.example.pfabackend.entities.Task;
import com.example.pfabackend.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByKanbanId(Long kanbanId);
}