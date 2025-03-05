package com.jobdev.dataharvest.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobdev.dataharvest.dto.BookFindDTO;
import com.jobdev.dataharvest.dto.BookSaveDTO;
import com.jobdev.dataharvest.entity.Book;
import com.jobdev.dataharvest.repository.BookRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

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
    public ResponseEntity<BookSaveDTO> sync(BookSaveDTO bookSaveDTO) {
        try {
            var existingBook = bookRepository.findByRefKey(bookSaveDTO.getRefKey());

            Book book;
            if (existingBook.isPresent()) {
                book = existingBook.get();
                if (bookSaveDTO.getTitle() != null && !bookSaveDTO.getTitle().isEmpty()) {
                    book.setTitle(bookSaveDTO.getTitle());
                }
            } else {
                book = bookSaveDTO.toEntity();
            }
            var authors = bookSaveDTO.getAuthors().stream().map(authorService::sync).collect(Collectors.toSet());
            book.setAuthors(authors);

            var savedBook = bookRepository.save(book);
            var responseBody = BookSaveDTO.fromEntity(savedBook);
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            throw e;
        }
    }
}
