package com.jobdev.dataharvest.dto;

import java.util.Set;

import com.jobdev.dataharvest.entity.Work;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkSaveDTO {
    private String refKey;
    private String title;
    private Set<AuthorSaveDTO> authors;

    public static WorkSaveDTO fromEntity(Work work) {
        return WorkSaveDTO.builder()
                .refKey(work.getRefKey())
                .build();
    }

    public Work toEntity() {
        return Work.builder()
                .refKey(this.getRefKey())
                .title(this.getTitle())
                .authors(AuthorSaveDTO.toEntities(this.getAuthors()))
                .build();
    }
}
