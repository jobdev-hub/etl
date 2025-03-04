package com.jobdev.dataharvest.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import com.jobdev.dataharvest.batch.OpenLibraryItemReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenLibraryService {
    private final JobLauncher jobLauncher;
    private final Job openLibraryImportJob;
    private final OpenLibraryItemReader openLibraryItemReader;

    /**
     * Sincroniza todos os livros de um determinado assunto
     * 
     * @param subject   O assunto a ser sincronizado (ex: "programming")
     * @param batchSize Tamanho do lote por requisição
     */
    public void syncAllBooks(String subject, int batchSize) {
        try {
            // Inicializa o reader com os parâmetros necessários
            openLibraryItemReader.initialize(subject, batchSize);

            // Cria parâmetros únicos para o job (para permitir múltiplas execuções)
            JobParameters parameters = new JobParametersBuilder()
                    .addString("subject", subject)
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            // Executa o job
            jobLauncher.run(openLibraryImportJob, parameters);

        } catch (Exception e) {
            log.error("Erro ao sincronizar livros de {} com Open Library API", subject, e);
            throw new RuntimeException("Falha na sincronização com Open Library", e);
        }
    }
}