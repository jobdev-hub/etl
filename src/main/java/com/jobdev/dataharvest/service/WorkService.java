package com.jobdev.dataharvest.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobdev.dataharvest.dto.WorkFindDTO;
import com.jobdev.dataharvest.dto.WorkSaveDTO;
import com.jobdev.dataharvest.entity.Work;
import com.jobdev.dataharvest.repository.WorkRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WorkService {

    private final WorkRepository workRepository;
    private final AuthorService authorService;

    public ResponseEntity<List<WorkFindDTO>> find(@PageableDefault Pageable pageable) {
        try {
            var works = workRepository.findAll(pageable).getContent();
            var responseBody = works.stream().map(WorkFindDTO::fromEntity).collect(Collectors.toList());
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public ResponseEntity<WorkSaveDTO> sync(WorkSaveDTO workSaveDTO) {
        try {
            var existingWork = workRepository.findByRefKey(workSaveDTO.getRefKey());

            Work work;
            if (existingWork.isPresent()) {
                work = existingWork.get();
                if (workSaveDTO.getTitle() != null && !workSaveDTO.getTitle().isEmpty()) {
                    work.setTitle(workSaveDTO.getTitle());
                }
            } else {
                work = workSaveDTO.toEntity();
            }
            var authors = workSaveDTO.getAuthors().stream().map(authorService::sync).collect(Collectors.toSet());
            work.setAuthors(authors);

            var savedWork = workRepository.save(work);
            var responseBody = WorkSaveDTO.fromEntity(savedWork);
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            throw e;
        }
    }
}
