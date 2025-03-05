package com.jobdev.dataharvest.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.jobdev.dataharvest.entity.Author;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorFindDTO {
    private String refKey;
    private String name;

    public static AuthorFindDTO fromEntity(Author author) {
        return AuthorFindDTO.builder()
                .refKey(author.getRefKey())
                .name(author.getName())
                .build();
    }

    public static Set<AuthorFindDTO> fromEntities(Set<Author> authors) {
        return authors.stream().map(AuthorFindDTO::fromEntity).collect(Collectors.toSet());
    }
}
