package com.jobdev.dataharvest.model.dto;

import java.util.Set;

import com.jobdev.dataharvest.model.entity.Book;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookFindDTO {
    private String tittle;
    private Set<AuthorFindDTO> authors;

    public static BookFindDTO fromEntity(Book book) {
        return BookFindDTO.builder()
                .tittle(book.getTitle())
                .authors(AuthorFindDTO.fromEntities(book.getAuthors()))
                .build();
    }
}
