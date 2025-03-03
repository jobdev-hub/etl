package com.jobdev.dataharvest.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class OpenLibraryWorkDTO {
    private String key;
    private String title;
    private List<OpenLibraryAuthorDTO> authors;

    /**
     * Retorna a chave sem o prefixo "/works/"
     * 
     * @return chave processada
     */
    public String getKey() {
        if (key == null) {
            return null;
        }

        // Remove o prefixo "/works/" se presente
        String prefix = "/works/";
        return key.startsWith(prefix) ? key.substring(prefix.length()) : key;
    }
}
