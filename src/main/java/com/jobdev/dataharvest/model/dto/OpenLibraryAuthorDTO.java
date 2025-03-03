package com.jobdev.dataharvest.model.dto;

import lombok.Data;

@Data
public class OpenLibraryAuthorDTO {
    private String key;
    private String name;

    /**
     * Retorna a chave sem o prefixo "/authors/"
     * 
     * @return chave processada
     */
    public String getKey() {
        if (key == null) {
            return null;
        }

        // Remove o prefixo "/authors/" se presente
        String prefix = "/authors/";
        return key.startsWith(prefix) ? key.substring(prefix.length()) : key;
    }
}
