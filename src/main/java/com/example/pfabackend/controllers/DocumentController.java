package com.example.pfabackend.controllers;

import com.example.pfabackend.entities.Comment;
import com.example.pfabackend.entities.Document;
import com.example.pfabackend.entities.Task;
import com.example.pfabackend.exceptions.ResourceNotFoundException;
import com.example.pfabackend.repositories.DocumentRepository;
import com.example.pfabackend.services.DocumentService;
import com.example.pfabackend.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocumentRepository documentRepository;

    @PostMapping(value = "/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createDocument(
            @PathVariable Long taskId,
            // @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(required = false) String assigneeEmail) { // Récupère les fichiers
        try {
            // Task createdTask = taskService.createTask(kanbanId, task, assigneeEmail);
            List<Document> docments = new ArrayList<>();
            // Sauvegarde des fichiers, si présents
            if (files != null) {
                for (MultipartFile file : files) {
                    docments.add(documentService.store(file, taskId)); // Enregistrement du document lié à la tâche
                }
            }

            return ResponseEntity.ok(docments);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne lors de la création de la tâche."));
        }
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) throws IOException {
        Resource resource = documentService.loadDocumentAsResource(id);
        Document doc = documentService.getDocumentById(id);
        System.out.println("doc******************* 1: " + doc.getFilePath());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getName() + "\"")
                .body(resource);
    }

    // Ouvrir un document dans un nouvel onglet
    @GetMapping("/{id}/open")
    @PreAuthorize("isAuthenticated()")
    // const url = `${TASK_API_URL}/documents/${documentId}/open`;
    public ResponseEntity<Resource> openDocument(@PathVariable Long id) throws IOException {
        Resource resource = documentService.loadDocumentAsResource(id);
        Document doc = documentService.getDocumentById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getName() + "\"")
                .body(resource);
    }
}