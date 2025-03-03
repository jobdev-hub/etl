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

    /**
     * Retorna o nome do autor considerando 255 caracteres no máximo
     * 
     * @return nome do autor
     */
    public String getName() {
        if (name == null) {
            return null;
        }

        return name.length() > 255 ? name.substring(0, 255) : name;
    }
}
