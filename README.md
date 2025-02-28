## DataHarvest
- Sistema de integração massiva de dados com Oracle DB
- Sincronização eficiente de 200K+ registros
- Java 21
- Spring Batch
- Oracle Database
- JPA
- Hibernate (auto-ddl)
- Swagger
- Lombok
- Open Library API (openlibrary.org)


### Extração de dados
GET https://openlibrary.org/subjects/programming.json?limit=1000

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
    private List<Author> authors;
}

public class Author {
    private UUID id;
    private String refKey; // UNIQUE (REF UPDATE)
    private String name;
}
```