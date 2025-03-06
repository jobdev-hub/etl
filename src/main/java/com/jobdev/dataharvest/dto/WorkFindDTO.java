package com.jobdev.dataharvest.dto;

import java.util.Set;

import com.jobdev.dataharvest.entity.Work;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkFindDTO {
    private String refKey;
    private String title;
    private Set<AuthorFindDTO> authors;

    public static WorkFindDTO fromEntity(Work work) {
        return WorkFindDTO.builder()
                .refKey(work.getRefKey())
                .title(work.getTitle())
                .authors(AuthorFindDTO.fromEntities(work.getAuthors()))
                .build();
    }
}
