package com.jobdev.dataharvest.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.jobdev.dataharvest.dto.OpenLibraryWorkDTO;
import com.jobdev.dataharvest.dto.WorkSaveDTO;
import com.jobdev.dataharvest.enums.JobName;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SyncWorkJobConfig {

    private final SyncWorkReader syncWorkReader;
    private final SyncWorkProcessor syncWorkProcessor;
    private final SyncWorkWriter syncWorkWriter;
    
    private static final int CHUNK_SIZE = 500;
    private static final int SKIP_LIMIT = 10;
    private static final String STEP_NAME = "syncWorkStep";

    @Bean
    public Job syncWorkJob(JobRepository jobRepository, Step syncWorkStep) {
        return new JobBuilder(JobName.SYNC_WORK.getName(), jobRepository)
                .start(syncWorkStep)
                .build();
    }

    @Bean
    public Step syncWorkStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<OpenLibraryWorkDTO, WorkSaveDTO>chunk(CHUNK_SIZE, transactionManager)
                .reader(syncWorkReader)
                .processor(syncWorkProcessor)
                .writer(syncWorkWriter)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .skip(Exception.class)
                .build();
    }

}
