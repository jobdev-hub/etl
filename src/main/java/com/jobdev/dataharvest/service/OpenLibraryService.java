package com.jobdev.dataharvest.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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

    /**
     * Sincroniza todos os livros de um determinado assunto
     * 
     * @param subject   O assunto a ser sincronizado (ex: "programming")
     * @param batchSize Tamanho do lote por requisição
     */
    public void syncAllBooks(String subject, int batchSize) {
        try {
            // Primeira requisição para obter o total de livros
            int totalBooks = getTotalBookCount(subject);
            if (totalBooks <= 0) {
                return;
            }

            // Contador global para livros processados com sucesso
            AtomicInteger totalProcessed = new AtomicInteger(0);

            // Processa em lotes
            for (int offset = 0; offset < totalBooks; offset += batchSize) {
                int processedInBatch = processBatch(subject, batchSize, offset, totalBooks);
                totalProcessed.addAndGet(processedInBatch);

                // Status de progresso
                logProgress(totalProcessed.get(), totalBooks);

                // Pausa entre requisições
                sleepBetweenRequests(500);
            }

            log.info("Sincronização completa! Total de livros processados: {}/{}", totalProcessed.get(), totalBooks);

        } catch (Exception e) {
            log.error("Erro ao sincronizar livros de {} com Open Library API", subject, e);
            throw new RuntimeException("Falha na sincronização com Open Library", e);
        }
    }

    private int getTotalBookCount(String subject) {
        var url = String.format("%s/subjects/%s.json?limit=0", openLibraryBaseUrl, subject);
        log.info("Consultando total de livros: {}", url);

        try {
            var response = restTemplate.getForEntity(url, OpenLibraryResponseDTO.class);

            // Verifica o status HTTP
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("API retornou status de erro: {}", response.getStatusCode());
                throw new RuntimeException("API retornou status de erro: " + response.getStatusCode());
            }

            // Usa Optional para tratar o body com mais segurança
            OpenLibraryResponseDTO body = Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> {
                        log.error("Resposta inválida da API ao consultar total de livros");
                        return new RuntimeException("Resposta inválida da API");
                    });

            // Usa Optional para tratar o workCount com segurança
            Integer workCount = Optional.ofNullable(body.getWork_count())
                    .orElseThrow(() -> {
                        log.error("Contagem de trabalhos nula na resposta da API");
                        return new RuntimeException("Resposta inválida da API - contagem nula");
                    });

            int total = workCount;
            log.info("Total de livros para sincronização: {}", total);
            return total;
        } catch (Exception e) {
            log.error("Erro ao obter contagem de livros", e);
            throw new RuntimeException("Falha ao obter contagem de livros", e);
        }
    }

    private int processBatch(String subject, int batchSize, int offset, int totalBooks) {
        int currentLimit = Math.min(batchSize, totalBooks - offset);
        log.info("Sincronizando lote: offset={}, limit={}", offset, currentLimit);

        var url = String.format("%s/subjects/%s.json?limit=%d&offset=%d",
                openLibraryBaseUrl, subject, currentLimit, offset);

        try {
            var response = restTemplate.getForEntity(url, OpenLibraryResponseDTO.class);

            return Optional.ofNullable(response.getBody())
                    .map(OpenLibraryResponseDTO::getWorks)
                    .filter(works -> !works.isEmpty())
                    .map(works -> {
                        log.info("Recebidos {} livros da API (offset={})", works.size(), offset);
                        return processBooks(works);
                    })
                    .orElseGet(() -> {
                        log.warn("Nenhum livro encontrado ou resposta inválida para offset={}", offset);
                        return 0;
                    });
        } catch (Exception e) {
            log.error("Erro ao processar lote (offset={})", offset, e);
            return 0; // Continua com o próximo lote mesmo em caso de falha
        }
    }

    private void logProgress(int processed, int total) {
        double percentage = (processed * 100.0) / total;
        String formattedPercentage = String.format("%.2f", percentage);
        log.info("Progresso: {}/{} livros sincronizados ({}%)",
                processed, total, formattedPercentage);
    }

    private void sleepBetweenRequests(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Processa uma lista de livros e retorna a quantidade processada com sucesso
     */
    private int processBooks(List<OpenLibraryWorkDTO> works) {
        int successful = 0;

        for (OpenLibraryWorkDTO work : works) {
            try {
                BookSaveDTO bookDTO = convertToBookDTO(work);
                bookService.sync(bookDTO);
                successful++;
            } catch (Exception e) {
                log.error("Erro ao processar livro: {}", work.getKey(), e);
                // Continua com o próximo livro
            }
        }

        log.info("Lote concluído: {} de {} livros processados com sucesso", successful, works.size());
        return successful;
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