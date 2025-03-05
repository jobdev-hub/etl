package com.jobdev.dataharvest.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobdev.dataharvest.dto.AuthorFindDTO;
import com.jobdev.dataharvest.service.AuthorService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/author")
@AllArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<AuthorFindDTO>> find(@PageableDefault Pageable pageable) {
        return authorService.find(pageable);
    }

}
