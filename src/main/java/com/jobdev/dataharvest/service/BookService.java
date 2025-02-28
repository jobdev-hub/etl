package com.jobdev.dataharvest.service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobdev.dataharvest.model.dto.AuthorSaveDTO;
import com.jobdev.dataharvest.model.dto.BookFindDTO;
import com.jobdev.dataharvest.model.dto.BookSaveDTO;
import com.jobdev.dataharvest.model.entity.Author;
import com.jobdev.dataharvest.model.entity.Book;
import com.jobdev.dataharvest.repository.AuthorRepository;
import com.jobdev.dataharvest.repository.BookRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public ResponseEntity<List<BookFindDTO>> find(@PageableDefault Pageable pageable) {
        try {
            var books = bookRepository.findAll(pageable).getContent();
            var responseBody = books.stream().map(BookFindDTO::fromEntity).collect(Collectors.toList());
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public ResponseEntity<BookSaveDTO> create(BookSaveDTO bookSaveDTO) {
        try {
            Book bookEntity = Book.builder()
                    .refKey(bookSaveDTO.getRefKey())
                    .title(bookSaveDTO.getTitle())
                    .authors(new HashSet<Author>())
                    .build();
            
            if (bookSaveDTO.getAuthors() != null && !bookSaveDTO.getAuthors().isEmpty()) {
                for (AuthorSaveDTO authorDTO : bookSaveDTO.getAuthors()) {
                    var existingAuthor = authorRepository.findByRefKey(authorDTO.getRefKey());
                    
                    Author author;
                    if (existingAuthor.isPresent()) {
                        author = existingAuthor.get();
                        
                        if (authorDTO.getName() != null && !authorDTO.getName().isEmpty()) {
                            author.setName(authorDTO.getName());
                            author = authorRepository.save(author);
                        }
                    } else {
                        author = authorDTO.toEntity();
                        author = authorRepository.save(author);
                    }
                    
                    bookEntity.getAuthors().add(author);
                }
            }
            
            Book savedBook = bookRepository.save(bookEntity);
            
            BookSaveDTO responseBody = BookSaveDTO.fromEntity(savedBook);
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            throw e;
        }
    }
}
