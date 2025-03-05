package com.jobdev.dataharvest.dto;

import java.util.Set;

import com.jobdev.dataharvest.entity.Book;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookSaveDTO {
    private String refKey;
    private String title;
    private Set<AuthorSaveDTO> authors;

    public static BookSaveDTO fromEntity(Book book) {
        return BookSaveDTO.builder()
                .refKey(book.getRefKey())
                .build();
    }

    public Book toEntity() {
        return Book.builder()
                .refKey(this.getRefKey())
                .title(this.getTitle())
                .authors(AuthorSaveDTO.toEntities(this.getAuthors()))
                .build();
    }
}
