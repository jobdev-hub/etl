package com.jobdev.dataharvest.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobdev.dataharvest.dto.WorkFindDTO;
import com.jobdev.dataharvest.service.WorkService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/work")
@AllArgsConstructor
public class WorkController {

    private final WorkService workService;

    @GetMapping
    public ResponseEntity<List<WorkFindDTO>> find(@PageableDefault Pageable pageable) {
        return workService.find(pageable);
    }

}
