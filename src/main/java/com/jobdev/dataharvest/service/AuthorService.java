package com.jobdev.dataharvest.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.jobdev.dataharvest.dto.AuthorFindDTO;
import com.jobdev.dataharvest.repository.AuthorRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public ResponseEntity<List<AuthorFindDTO>> find(@PageableDefault Pageable pageable) {
        try {
            var authors = authorRepository.findAll(pageable).getContent();
            var responseBody = authors.stream().map(AuthorFindDTO::fromEntity).collect(Collectors.toList());
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            throw e;
        }
    }
}
