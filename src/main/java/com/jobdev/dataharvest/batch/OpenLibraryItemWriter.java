package com.jobdev.dataharvest.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.jobdev.dataharvest.model.dto.BookSaveDTO;
import com.jobdev.dataharvest.service.BookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenLibraryItemWriter implements ItemWriter<BookSaveDTO> {

    private final BookService bookService;

    @Override
    public void write(@NonNull Chunk<? extends BookSaveDTO> chunk) throws Exception {
        int success = 0;
        int failed = 0;

        log.info("Persistindo pedaço do lote de {} livros", chunk.size());

        for (BookSaveDTO book : chunk) {
            try {
                bookService.sync(book);
                success++;
            } catch (Exception e) {
                log.error("Erro ao salvar livro: {}", book.getRefKey(), e);
                failed++;
            }
        }

        log.info("Pedaço do lote gravado: {} processados com sucesso, {} falhas", success, failed);
    }
}