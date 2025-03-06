package com.jobdev.dataharvest.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.jobdev.dataharvest.dto.WorkSaveDTO;
import com.jobdev.dataharvest.service.WorkService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncWorkWriter implements ItemWriter<WorkSaveDTO> {

    private final WorkService workService;

    @Override
    public void write(@NonNull Chunk<? extends WorkSaveDTO> chunk) throws Exception {
        int success = 0;
        int failed = 0;

        log.info("Persistindo pedaço do lote de {} livros", chunk.size());

        for (WorkSaveDTO work : chunk) {
            try {
                workService.sync(work);
                success++;
            } catch (Exception e) {
                log.error("Erro ao salvar livro: {}", work.getRefKey(), e);
                failed++;
            }
        }

        log.info("Pedaço do lote gravado: {} processados com sucesso, {} falhas", success, failed);
    }
}