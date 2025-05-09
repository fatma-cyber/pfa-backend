package com.example.pfabackend.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.pfabackend.entities.Document;
import com.example.pfabackend.entities.Kanban;
import com.example.pfabackend.entities.Task;
import com.example.pfabackend.exceptions.ResourceNotFoundException;
import com.example.pfabackend.repositories.DocumentRepository;
import com.example.pfabackend.repositories.TaskRepository;

import jakarta.annotation.PostConstruct;

@Service
public class DocumentService {
    private final Path uploadDir = Paths.get("uploads");
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private TaskRepository taskRepository;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadDir);
    }

    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
    }

    public Document store(MultipartFile file, Long taskId) throws IOException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("task non trouvé avec l'ID : " + taskId));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String contentType = file.getContentType(); // par exemple "application/pdf"
        Path filePath = uploadDir.resolve(fileName); // Gère la résolution du chemin du fichier dans le répertoire de

        // stockage.

        // Sauvegarde le fichier dans le système de fichiers
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Crée un nouveau document
        Document doc = new Document();
        doc.setName(file.getOriginalFilename());
        // Ne stocke que le nom du fichier (pas /uploads/)
        doc.setFilePath(fileName); // Stocke seulement le nom de fichier ici, pas le chemin absolu
        doc.setFileType(contentType);
        doc.setTask(task);

        // Sauvegarde le document en base de données
        return documentRepository.save(doc);
    }

    public List<Document> getDocumentsByTaskId(Long taskId) {
        return documentRepository.findByTaskId(taskId).stream()
                .map(doc -> new Document(doc.getId(), doc.getName(), doc.getFileType()))
                .collect(Collectors.toList());
    }

    public Resource loadDocumentAsResource(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        System.out.println("➡️ FileName: " + document.getFilePath()); // DEBUG

        // Charge le fichier avec un chemin relatif
        Path uploadsDir = Paths.get("uploads").toAbsolutePath().normalize();
        Path filePath = uploadsDir.resolve(document.getFilePath()).normalize();

        // Retourne la ressource à partir du chemin absolu du fichier
        return new UrlResource(filePath.toUri());
    }

    /*
     * public Document storeFile(MultipartFile file, Task task) throws IOException {
     * String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
     * Path filePath = uploadDir.resolve(fileName);
     * Files.copy(file.getInputStream(), filePath,
     * StandardCopyOption.REPLACE_EXISTING);
     * 
     * Document doc = new Document();
     * doc.setName(file.getOriginalFilename());
     * doc.setFilePath("/uploads/" + fileName);
     * doc.setTask(task);
     * return documentRepository.save(doc);
     * }
     */
}
