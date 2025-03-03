package com.jobdev.dataharvest.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class OpenLibraryWorkDTO {
    private String key;
    private String title;
    private List<OpenLibraryAuthorDTO> authors;    
}
