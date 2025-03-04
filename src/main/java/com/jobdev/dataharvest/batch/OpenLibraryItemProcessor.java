package com.jobdev.dataharvest.batch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.jobdev.dataharvest.model.dto.AuthorSaveDTO;
import com.jobdev.dataharvest.model.dto.BookSaveDTO;
import com.jobdev.dataharvest.model.dto.OpenLibraryAuthorDTO;
import com.jobdev.dataharvest.model.dto.OpenLibraryWorkDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpenLibraryItemProcessor implements ItemProcessor<OpenLibraryWorkDTO, BookSaveDTO> {

    @Override
    public BookSaveDTO process(@NonNull OpenLibraryWorkDTO work) throws Exception {
        try {
            return convertToBookDTO(work);
        } catch (Exception e) {
            log.error("Erro ao processar livro: {}", work.getKey(), e);
            throw e;
        }
    }

    private BookSaveDTO convertToBookDTO(OpenLibraryWorkDTO work) {
        return BookSaveDTO.builder()
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
