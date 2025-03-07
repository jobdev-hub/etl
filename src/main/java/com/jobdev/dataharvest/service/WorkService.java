package com.jobdev.dataharvest.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.jobdev.dataharvest.dto.WorkFindDTO;
import com.jobdev.dataharvest.repository.WorkRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class WorkService {

    private final WorkRepository workRepository;

    public ResponseEntity<List<WorkFindDTO>> find(@PageableDefault Pageable pageable) {
        try {
            var works = workRepository.findAll(pageable).getContent();
            var responseBody = works.stream().map(WorkFindDTO::fromEntity).collect(Collectors.toList());
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            throw e;
        }
    }
}
