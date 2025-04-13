package com.example.pfabackend.services;

import com.example.pfabackend.dto.TaskDto;
import com.example.pfabackend.entities.*;
import com.example.pfabackend.repositories.TaskRepository;
//import com.example.pfabackend.repositories.TaskRepositoryDto;
import com.example.pfabackend.repositories.CommentRepository;
import com.example.pfabackend.repositories.UserRepository;
import jakarta.transaction.Transactional;
//import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    /* @Autowired
    private TaskRepositoryDto taskRepositoryDto; */
    /* @Autowired
    private TaskRepository taskRepository; */

    @Autowired
    private UserRepository userRepository;

   // @Autowired
    //private ColumnKanbanRepository columnKanbanRepository;

    @Autowired
    private CommentRepository commentRepository;

    // ➕ Ajouter une tâche
    /* public TaskDto addTask(TaskDto task) {
        return taskRepositoryDto.save(task);
        //return taskRepository.save(task);
    } */
    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

    // ✏️ Modifier une tâche
    public Task updateTask(Long taskId, Task newTaskData) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTitle(newTaskData.getTitle());
            task.setDescription(newTaskData.getDescription());
            task.setStatus(newTaskData.getStatus());
            task.setPriority(newTaskData.getPriority());
            task.setDeadline(newTaskData.getDeadline());
            task.setColor(newTaskData.getColor());
            task.setStoryPoints(newTaskData.getStoryPoints());
            task.setAssignee(newTaskData.getAssignee());
            //task.setColumn(newTaskData.getColumn());
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Tâche non trouvée avec ID : " + taskId);
        }
    }

    // 💬 Ajouter un commentaire à une tâche, avec un utilisateur (auteur)
    public Comment addCommentToTask(Long taskId, Comment comment, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID : " + taskId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID : " + userId));

        comment.setTask(task);     // Associe le commentaire à la tâche
        comment.setAuthor(user);   // Associe le commentaire à l'utilisateur (auteur)

        return commentRepository.save(comment);
    }
}
