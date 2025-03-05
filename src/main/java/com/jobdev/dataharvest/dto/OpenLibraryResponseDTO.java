package com.jobdev.dataharvest.dto;

import java.util.List;

import lombok.Data;

@Data
public class OpenLibraryResponseDTO {
    private String key;
    private String name;
    private String subject_type;
    private int work_count;
    private List<OpenLibraryWorkDTO> works;    
}
