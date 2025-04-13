package com.example.pfabackend.controllers;

import com.example.pfabackend.dto.TaskDto;
import com.example.pfabackend.entities.Comment;
import com.example.pfabackend.entities.Task;import com.example.pfabackend.services.TaskService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // ➕ Ajouter une tâche

    @PostMapping("/addTask")
    public String addTask(@RequestBody Task task) {
    //public TaskDto addTask(@RequestBody TaskDto task) {
        taskService.addTask(task);
        //return taskService.addTask(task);
        return "tache ajouté";
    }

    /*
     * // ✏️ Modifier une tâche
     * 
     * @RequestMapping("/updateTask")
     * 
     * @PutMapping("/{taskId}")
     * public Task updateTask(@PathVariable Long taskId, @RequestBody Task
     * newTaskData) {
     * return taskService.updateTask(taskId, newTaskData);
     * }
     * 
     * // 💬 Ajouter un commentaire avec utilisateur
     * 
     * @PostMapping("/{taskId}/comments/{userId}")
     * public Comment addCommentToTask(
     * 
     * @PathVariable Long taskId,
     * 
     * @PathVariable Long userId,
     * 
     * @RequestBody Comment comment) {
     * return taskService.addCommentToTask(taskId, comment, userId);
     * }
     */
}
