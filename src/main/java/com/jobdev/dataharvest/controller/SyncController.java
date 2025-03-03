package com.jobdev.dataharvest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobdev.dataharvest.service.OpenLibraryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncController {

    private final OpenLibraryService openLibraryService;

    @PostMapping("open-library/books")
    public ResponseEntity<String> syncAllBooks(
            @RequestParam(defaultValue = "programming") String subject,
            @RequestParam(defaultValue = "1000") int batchSize) {
            
        // Este método é executado de forma assíncrona para não bloquear a resposta HTTP
        new Thread(() -> {
            openLibraryService.syncAllBooks(subject, batchSize);
        }).start();
        
        return ResponseEntity.ok("Sincronização completa iniciada para todos os livros de " + subject + 
                " (processando em lotes de " + batchSize + ")");
    }
}
