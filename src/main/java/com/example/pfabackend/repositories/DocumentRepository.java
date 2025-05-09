package com.example.pfabackend.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.pfabackend.entities.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByTaskId(Long taskId);

    Document findByName(String name);
}
