package com.jobdev.dataharvest.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OpenLibraryResponseDTO {
    private String key;
    private String name;

    @JsonProperty("subject_type")
    private String subjectType;

    @JsonProperty("work_count")
    private int workCount;

    private List<OpenLibraryWorkDTO> works;
}
