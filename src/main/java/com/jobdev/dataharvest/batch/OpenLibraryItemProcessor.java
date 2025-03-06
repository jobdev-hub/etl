package com.jobdev.dataharvest.batch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.jobdev.dataharvest.dto.AuthorSaveDTO;
import com.jobdev.dataharvest.dto.OpenLibraryAuthorDTO;
import com.jobdev.dataharvest.dto.OpenLibraryWorkDTO;
import com.jobdev.dataharvest.dto.WorkSaveDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpenLibraryItemProcessor implements ItemProcessor<OpenLibraryWorkDTO, WorkSaveDTO> {

    @Override
    public WorkSaveDTO process(@NonNull OpenLibraryWorkDTO work) throws Exception {
        try {
            return convertToWorkSaveDTO(work);
        } catch (Exception e) {
            log.error("Erro ao processar livro: {}", work.getKey(), e);
            throw e;
        }
    }

    private WorkSaveDTO convertToWorkSaveDTO(OpenLibraryWorkDTO work) {
        return WorkSaveDTO.builder()
                .refKey(work.getKey())
                .title(work.getTitle())
                .authors(convertAuthors(work.getAuthors()))
                .build();
    }

    private Set<AuthorSaveDTO> convertAuthors(List<OpenLibraryAuthorDTO> authors) {
        if (authors == null) {
            return new HashSet<>();
        }

        return authors.stream()
                .map(author -> AuthorSaveDTO.builder()
                        .refKey(author.getKey())
                        .name(author.getName())
                        .build())
                .collect(Collectors.toSet());
    }
}
