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
    public ResponseEntity<String> syncBooks(
            @RequestParam(required = true, defaultValue = "100") int limit,
            @RequestParam(required = true) String subject) {

        openLibraryService.syncBooks(limit, "");
        return ResponseEntity.ok("Sincronização iniciada para " + limit + " livros de programação");
    }

}
