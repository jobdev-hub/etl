package com.jobdev.dataharvest.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.jobdev.dataharvest.dto.OpenLibraryWorkDTO;
import com.jobdev.dataharvest.service.OpenLibraryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncWorkReader implements ItemReader<OpenLibraryWorkDTO> {

    private final OpenLibraryService openLibraryService;

    private List<OpenLibraryWorkDTO> workList = new ArrayList<>();
    private AtomicInteger index = new AtomicInteger(0);
    private int offset = 0;
    private int batchSize = 1000;
    private String subject;
    private int total;
    private boolean initialized = false;

    public void initialize(String subject, int batchSize) {
        this.subject = subject;
        this.batchSize = batchSize;
        this.offset = 0;
        this.total = openLibraryService.getTotalWorkCount(subject);
        this.initialized = true;
        log.info("ItemReader inicializado para {} - total de livros: {}", subject, total);
    }

    @Override
    public OpenLibraryWorkDTO read() throws Exception {
        if (!initialized) {
            throw new IllegalStateException("ItemReader não foi inicializado. Chame initialize() primeiro.");
        }

        if (index.get() >= workList.size()) {
            if (offset >= total) {
                return null;
            }

            fetchNextBatch();

            if (workList.isEmpty()) {
                return null;
            }
        }

        return workList.get(index.getAndIncrement());
    }

    private void fetchNextBatch() {
        int limit = Math.min(batchSize, total - offset);

        log.info("Sincronizando lote: offset={}, limit={}", offset, limit);
        workList = openLibraryService.fetchWorks(subject, limit, offset);

        index.set(0);
        offset += workList.size();

        log.info("Recebidos {} livros (total processado: {}/{})", workList.size(), offset, total);
    }

}