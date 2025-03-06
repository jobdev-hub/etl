package com.jobdev.dataharvest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jobdev.dataharvest.dto.OpenLibraryResponseDTO;
import com.jobdev.dataharvest.dto.OpenLibraryWorkDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável por interagir com a API do Open Library.
 * Centraliza todas as chamadas à API externa.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpenLibraryService {

    private final RestTemplate restTemplate;

    @Value("${integration.openlibrary.url:https://openlibrary.org}")
    private String baseUrl;

    /**
     * Obtém o total de obras disponíveis para um determinado assunto.
     * 
     * @param subject O assunto a ser pesquisado
     * @return O número total de obras para esse assunto
     * @throws RuntimeException se houver erro na comunicação com a API
     */
    public int getTotalWorkCount(String subject) {
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

            Integer workCount = Optional.ofNullable(body.getWorkCount())
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

    /**
     * Busca um lote de obras da Open Library para um determinado assunto.
     * 
     * @param subject O assunto a ser pesquisado
     * @param limit   O número máximo de obras a serem retornadas
     * @param offset  A posição inicial para paginação
     * @return Lista de obras encontradas
     */
    public List<OpenLibraryWorkDTO> fetchWorks(String subject, int limit, int offset) {
        var url = String.format("%s/subjects/%s.json?limit=%d&offset=%d", baseUrl, subject, limit, offset);
        log.info("Buscando lote: offset={}, limit={}, url={}", offset, limit, url);

        try {
            var response = restTemplate.getForEntity(url, OpenLibraryResponseDTO.class);

            return Optional.ofNullable(response)
                    .map(ResponseEntity::getBody)
                    .map(OpenLibraryResponseDTO::getWorks)
                    .orElseGet(() -> {
                        log.warn("Resposta vazia da API para offset={}", offset);
                        return List.of();
                    });

        } catch (Exception e) {
            log.error("Erro ao buscar lote de livros (offset={})", offset, e);
            return List.of();
        }
    }
}