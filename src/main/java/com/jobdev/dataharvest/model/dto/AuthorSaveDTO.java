package com.jobdev.dataharvest.model.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.jobdev.dataharvest.model.entity.Author;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorSaveDTO {
    private String refKey;
    private String name;

    public static AuthorSaveDTO fromEntity(Author author) {
        return AuthorSaveDTO.builder()
                .refKey(author.getRefKey())
                .build();
    }

    public Author toEntity() {
        return Author.builder()
                .refKey(this.getRefKey())
                .name(this.getName())
                .build();
    }

    public static Set<Author> toEntities(Set<AuthorSaveDTO> authorSaveDTOs) {
        return authorSaveDTOs.stream().map(AuthorSaveDTO::toEntity).collect(Collectors.toSet());
    }
}
