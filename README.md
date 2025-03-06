## DataHarvest
- Sistema de integração massiva de dados com Oracle DB
- Java 21
- Spring Batch
- Oracle Database
- JPA
- Hibernate
- Swagger
- Lombok
- Open Library API


### Extração de dados
GET https://openlibrary.org/subjects/programming.json?limit=1000

- Exemplo de resposta (removendo campos nao utilizados)
```json
{
  "work_count": 9299,
  "works": [
    {
      "key": "/works/OL2829091W",
      "title": "Reconfigurable Processor Array A Bit Sliced Parallel Computer (USA)",
      "authors": [
        {
          "key": "/authors/OL422557A",
          "name": "A. Rushton"
        }
      ]
    }
  ]
}
```

### Carregamento de dados
```Java
public class Work {
    private UUID id;
    private String refKey; // UNIQUE (REF UPDATE)
    private String title;
    private Set<Author> authors;
}

public class Author {
    private UUID id;
    private String refKey; // UNIQUE (REF UPDATE)
    private String name;
}
```

## Arquitetura Spring Batch

### Jobs
- **syncWorkJob**: Realiza a sincronização de obras da Open Library com o banco Oracle

### Fluxo de Dados
1. **syncWorkReader**: Busca obras da API Open Library em lotes paginados
2. **syncWorkProcessor**: Converte DTOs da API para objetos de domínio
3. **syncWorkWriter**: Persiste os dados no banco Oracle