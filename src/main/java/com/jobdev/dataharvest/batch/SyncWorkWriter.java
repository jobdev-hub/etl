package com.jobdev.dataharvest.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.jobdev.dataharvest.dto.AuthorSaveDTO;
import com.jobdev.dataharvest.dto.WorkSaveDTO;
import com.jobdev.dataharvest.entity.Author;
import com.jobdev.dataharvest.entity.Work;
import com.jobdev.dataharvest.repository.AuthorRepository;
import com.jobdev.dataharvest.repository.WorkRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncWorkWriter implements ItemWriter<WorkSaveDTO> {

    private final WorkRepository workRepository;
    private final AuthorRepository authorRepository;

    @Override
    public void write(@NonNull Chunk<? extends WorkSaveDTO> chunk) throws Exception {
        List<? extends WorkSaveDTO> workList = chunk.getItems();
        try {
            List<Author> authors = authorRepository.saveAll(buildAuthors(workList));
            workRepository.saveAll(buildWorks(workList, authors));

        } catch (Exception e) {
            log.error("Erro ao processar lote de {} obras", workList.size(), e);
            throw e;
        }
    }

    private List<Author> buildAuthors(List<? extends WorkSaveDTO> workList) {
        // Extrair autores distintos das obras usando refKey como filtro
        Map<String, AuthorSaveDTO> distinctAuthorsByRefKey = workList.stream()
            .flatMap(work -> work.getAuthors().stream())
            .collect(Collectors.toMap(
                AuthorSaveDTO::getRefKey,
                Function.identity(),
                (existing, replacement) -> existing // Em caso de conflito, mantém o existente
            ));
        
        Set<String> authorsRefKeys = distinctAuthorsByRefKey.keySet();
        
        // Buscar autores já salvos no banco
        Map<String, Author> existingAuthors = authorRepository.findAllByRefKeyIn(authorsRefKeys).stream()
            .collect(Collectors.toMap(
                Author::getRefKey,
                Function.identity(),
                (existing, replacement) -> existing // Em caso de conflito, mantém o existente
            ));
        
        // Montar lista de autores para salvar
        List<Author> authors = new ArrayList<>(distinctAuthorsByRefKey.size());
        for (AuthorSaveDTO dto : distinctAuthorsByRefKey.values()) {
            Author author;
            if (existingAuthors.containsKey(dto.getRefKey())) {
                author = existingAuthors.get(dto.getRefKey());
                if (dto.getName() != null && !dto.getName().isEmpty()) {
                    author.setName(dto.getName());
                }
            } else {
                author = dto.toEntity();
            }
            authors.add(author);
        }
        
        return authors;
    }

    private List<Work> buildWorks(List<? extends WorkSaveDTO> workList, List<Author> savedAuthors) {
    // Mapear autores salvos por chave de referência, tratando duplicatas
    Map<String, Author> authorsMapByRefKey = savedAuthors.stream()
        .collect(Collectors.toMap(
            Author::getRefKey,
            Function.identity(),
            (existing, replacement) -> existing // Em caso de conflito, mantém o existente
        ));

        // Extrair chaves de referência das obras
        Set<String> worksRefKeys = workList.stream()
                .map(WorkSaveDTO::getRefKey)
                .collect(Collectors.toSet());

        // Buscar obras já salvas no banco
        Map<String, Work> existingWorks = workRepository.findAllByRefKeyIn(worksRefKeys).stream()
                .map(work -> Map.entry(work.getRefKey(), work))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Montar lista de obras para salvar
        List<Work> worksToSave = new ArrayList<>(workList.size());
        for (WorkSaveDTO dto : workList) {
            Work work;
            if (existingWorks.containsKey(dto.getRefKey())) {
                work = existingWorks.get(dto.getRefKey());
                if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
                    work.setTitle(dto.getTitle());
                }
            } else {
                work = dto.toEntity();
            }

            Set<Author> authors = dto.getAuthors().stream()
                    .map(authorDto -> authorsMapByRefKey.get(authorDto.getRefKey()))
                    .collect(Collectors.toSet());

            work.setAuthors(authors);
            worksToSave.add(work);
        }

        return worksToSave;
    }
}