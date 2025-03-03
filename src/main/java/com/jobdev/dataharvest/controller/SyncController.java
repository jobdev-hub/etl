package com.jobdev.dataharvest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobdev.dataharvest.service.OpenLibraryService;
import com.jobdev.dataharvest.util.ThreadUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncController {
    private final OpenLibraryService openLibraryService;

    @PostMapping("/open-library/books-and-authors")
    public ResponseEntity<String> syncAllBooks(
            @RequestParam(defaultValue = "programming") String subject,
            @RequestParam(defaultValue = "1000") int batchSize) {

        ThreadUtil.runAsync(() -> openLibraryService.syncAllBooks(subject, batchSize));
        return ResponseEntity.ok("Sincronização iniciada para " + subject + " (lotes de " + batchSize + ")");
    }
}
