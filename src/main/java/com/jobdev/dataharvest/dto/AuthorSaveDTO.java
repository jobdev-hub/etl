package com.jobdev.dataharvest.dto;

import com.jobdev.dataharvest.entity.Author;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorSaveDTO {
    private String refKey;
    private String name;

    public Author toEntity() {
        return Author.builder()
                .refKey(this.getRefKey())
                .name(this.getName())
                .build();
    }
}
