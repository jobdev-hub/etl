package com.jobdev.dataharvest.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.jobdev.dataharvest.dto.AuthorFindDTO;
import com.jobdev.dataharvest.dto.AuthorSaveDTO;
import com.jobdev.dataharvest.entity.Author;
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

    public Author sync(AuthorSaveDTO authorSaveDTO) {
        try {
            var existingAuthor = authorRepository.findByRefKey(authorSaveDTO.getRefKey());

            Author author;
            if (existingAuthor.isPresent()) {
                author = existingAuthor.get();
                if (authorSaveDTO.getName() != null && !authorSaveDTO.getName().isEmpty()) {
                    author.setName(authorSaveDTO.getName());
                }
            } else {
                author = authorSaveDTO.toEntity();
            }

            var savedAuthor = authorRepository.save(author);
            return savedAuthor;

        } catch (Exception e) {
            throw e;
        }
    }

}
