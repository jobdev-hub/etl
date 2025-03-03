## DataHarvest
- Sistema de integração massiva de dados com Oracle DB
- Java 21
- Spring Batch (PENDENTE)
- Oracle Database
- JPA
- Hibernate
- Swagger
- Lombok
- Open Library API


### Extração de dados
GET https://openlibrary.org/subjects/programming.json?limit=100

- Exemplo de resposta (removendo alguns campos)
```json
{
  "key": "/subjects/programming",
  "name": "programming",
  "subject_type": "subject",
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
public class Book {
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