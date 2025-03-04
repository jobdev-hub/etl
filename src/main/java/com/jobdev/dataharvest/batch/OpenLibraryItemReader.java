package com.jobdev.dataharvest.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jobdev.dataharvest.model.dto.OpenLibraryResponseDTO;
import com.jobdev.dataharvest.model.dto.OpenLibraryWorkDTO;
import com.jobdev.dataharvest.util.ThreadUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpenLibraryItemReader implements ItemReader<OpenLibraryWorkDTO> {

    private final RestTemplate restTemplate;
    private List<OpenLibraryWorkDTO> worksList;
    private AtomicInteger nextWorkIndex;
    private int offset = 0;
    private final int batchSize;
    private String subject;
    private int totalBooks;
    private boolean initialized = false;

    @Value("${integration.openlibrary.url:https://openlibrary.org}")
    private String baseUrl;

    public OpenLibraryItemReader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.batchSize = 1000;
        this.worksList = new ArrayList<>();
        this.nextWorkIndex = new AtomicInteger(0);
    }

    public void initialize(String subject, int batchSize) {
        this.subject = subject;
        this.offset = 0;
        this.totalBooks = getTotalBookCount(subject);
        this.initialized = true;
        log.info("ItemReader inicializado para {} - total de livros: {}", subject, totalBooks);
    }

    @Override
    public OpenLibraryWorkDTO read() throws Exception {
        if (!initialized) {
            throw new IllegalStateException("ItemReader não foi inicializado. Chame initialize() primeiro.");
        }

        // Se precisamos buscar mais livros
        if (nextWorkIndex.get() >= worksList.size()) {
            // Se já lemos todos os livros disponíveis
            if (offset >= totalBooks) {
                return null; // Sinaliza o fim do processamento para o Spring Batch
            }

            fetchNextBatch();

            // Se não há mais livros após a busca, encerramos
            if (worksList.isEmpty()) {
                return null;
            }
        }

        // Retorna o próximo livro e incrementa o índice
        return worksList.get(nextWorkIndex.getAndIncrement());
    }

    private void fetchNextBatch() {
        int currentLimit = Math.min(batchSize, totalBooks - offset);
        log.info("Sincronizando lote: offset={}, limit={}", offset, currentLimit);

        var url = String.format("%s/subjects/%s.json?limit=%d&offset=%d", baseUrl, subject, currentLimit, offset);

        try {
            var response = restTemplate.getForEntity(url, OpenLibraryResponseDTO.class);

            Optional.ofNullable(response)
                    .map(ResponseEntity::getBody)
                    .map(OpenLibraryResponseDTO::getWorks)
                    .ifPresentOrElse(this::processReceivedWorks, this::handleEmptyResponse);

        } catch (Exception e) {
            log.error("Erro ao buscar lote de livros (offset={})", offset, e);
            worksList = new ArrayList<>();
        }
    }

    private int getTotalBookCount(String subject) {
        var url = String.format("%s/subjects/%s.json?limit=0", baseUrl, subject);
        log.info("Consultando total de livros: {}", url);

        try {
            var response = restTemplate.getForEntity(url, OpenLibraryResponseDTO.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("API retornou status de erro: {}", response.getStatusCode());
                throw new RuntimeException("API retornou status de erro: " + response.getStatusCode());
            }

            OpenLibraryResponseDTO body = Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> {
                        log.error("Resposta inválida da API ao consultar total de livros");
                        return new RuntimeException("Resposta inválida da API");
                    });

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

    private void processReceivedWorks(List<OpenLibraryWorkDTO> works) {
        worksList = works;
        nextWorkIndex.set(0);
        offset += worksList.size();
        log.info("Recebidos {} livros (total processado: {}/{})", worksList.size(), offset, totalBooks);
        ThreadUtil.sleep(500);
    }

    private void handleEmptyResponse() {
        log.warn("Resposta vazia da API para offset={}", offset);
        worksList = new ArrayList<>();
    }

}
