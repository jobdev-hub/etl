package com.jobdev.dataharvest.dto;

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

    /**
     * Retorna o título da obra considerando 1000 caracteres no máximo
     * 
     * @return título da obra
     */
    public String getTitle() {
        if (title == null) {
            return null;
        }

        return title.length() > 1000 ? title.substring(0, 1000) : title;
    }
}
