package com.jobdev.dataharvest.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.jobdev.dataharvest.model.dto.BookSaveDTO;
import com.jobdev.dataharvest.model.dto.OpenLibraryWorkDTO;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OpenLibraryBatchConfig {

    private final OpenLibraryItemReader openLibraryReader;
    private final OpenLibraryItemProcessor openLibraryProcessor;
    private final OpenLibraryItemWriter openLibraryWriter;
    private static final int CHUNK_SIZE = 200;

    @Bean
    public Job openLibraryImportJob(JobRepository jobRepository, Step openLibraryStep) {
        return new JobBuilder("openLibraryImportJob", jobRepository)
                .start(openLibraryStep)
                .build();
    }

    @Bean
    public Step openLibraryStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("openLibraryStep", jobRepository)
                .<OpenLibraryWorkDTO, BookSaveDTO>chunk(CHUNK_SIZE, transactionManager)
                .reader(openLibraryReader)
                .processor(openLibraryProcessor)
                .writer(openLibraryWriter)
                .faultTolerant()
                .skipLimit(10)
                .skip(Exception.class)
                .build();
    }

}
