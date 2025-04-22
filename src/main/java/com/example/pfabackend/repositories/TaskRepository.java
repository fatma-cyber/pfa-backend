package com.example.pfabackend.repositories;

import com.example.pfabackend.entities.Task;
import com.example.pfabackend.entities.Kanban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByKanbanId(Long kanbanId);
    List<Task> findByAssigneeId(Long userId);
    List<Task> findByStatus(Task.Status status);
    List<Task> findByKanbanIdAndStatus(Long kanbanId, Task.Status status);
}