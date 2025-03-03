package com.jobdev.dataharvest.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jobdev.dataharvest.model.dto.AuthorSaveDTO;
import com.jobdev.dataharvest.model.dto.BookSaveDTO;
import com.jobdev.dataharvest.model.dto.OpenLibraryAuthorDTO;
import com.jobdev.dataharvest.model.dto.OpenLibraryResponseDTO;
import com.jobdev.dataharvest.model.dto.OpenLibraryWorkDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenLibraryService {
    private final RestTemplate restTemplate;
    private final BookService bookService;

    @Value("${integration.openlibrary.url:https://openlibrary.org}")
    private String openLibraryBaseUrl;

    public void syncBooks(int limit, String subjectPath) {
        try {
            var url = openLibraryBaseUrl + "/subjects/" + subjectPath + ".json?limit=" + limit;
            log.info("Consultando Open Library API: {}", url);

            var response = restTemplate.getForEntity(url, OpenLibraryResponseDTO.class);

            var body = response.getBody();
            if (body != null && body.getWorks() != null) {
                log.info("Recebidos {} livros da API", body.getWorks().size());
                processBooks(body.getWorks());
            } else {
                log.warn("Nenhum livro encontrado ou resposta inválida da API");
            }
        } catch (Exception e) {
            log.error("Erro ao sincronizar com Open Library API", e);
            throw new RuntimeException("Falha na sincronização com Open Library", e);
        }
    }

    private void processBooks(List<OpenLibraryWorkDTO> works) {
        int count = 0;
        int total = works.size();

        for (OpenLibraryWorkDTO work : works) {
            try {
                count++;
                log.debug("Processando livro {}/{}: {}", count, total, work.getTitle());

                BookSaveDTO bookDTO = BookSaveDTO.builder()
                        .refKey(work.getKey())
                        .title(work.getTitle())
                        .authors(convertAuthors(work.getAuthors()))
                        .build();

                bookService.sync(bookDTO);

            } catch (Exception e) {
                log.error("Erro ao processar livro: {}", work.getKey(), e);
            }
        }

        log.info("Sincronização concluída: {} de {} livros processados com sucesso", count, total);
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
