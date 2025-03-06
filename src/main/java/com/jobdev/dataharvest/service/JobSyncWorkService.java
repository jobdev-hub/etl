package com.jobdev.dataharvest.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import com.jobdev.dataharvest.batch.SyncWorkReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSyncWorkService {
    private final JobLauncher jobLauncher;
    private final Job syncWorkJob;
    private final SyncWorkReader syncWorkReader;

    public void syncWorks(String subject, int batchSize) {
        try {
            syncWorkReader.initialize(subject, batchSize);

            JobParameters parameters = new JobParametersBuilder()
                    .addString("subject", subject)
                    .addLong("timestamp", System.currentTimeMillis())
                    .addLong("batchSize", (long) batchSize)
                    .toJobParameters();

            log.info("Iniciando sincronização de obras para o assunto: {}", subject);
            jobLauncher.run(syncWorkJob, parameters);

        } catch (Exception e) {
            log.error("Erro ao sincronizar livros de {} com Open Library API", subject, e);
            throw new RuntimeException("Falha na sincronização com Open Library", e);
        }
    }
}