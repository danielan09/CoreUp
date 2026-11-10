# CoreUp Frontend

Interface estática em HTML, CSS e JavaScript para consultar alunos e gerir os respetivos exercícios através de uma API REST.

## Executar

O programa de terminal e a API têm pontos de entrada separados. `Main` inicia o menu de terminal; para usar a interface web, inicie `ApiMain` no IntelliJ. Com a API em execução, abra `http://localhost:8080` no navegador: o servidor publica o `index.html` e atende as chamadas da API.

Se a interface for servida em outra origem, configure a URL da API antes de `app.js` no `index.html`:

```html
<script>window.COREUP_API_BASE = "http://localhost:8080/api";</script>
```

O servidor escuta apenas conexões locais. A aplicação apresenta erros de conexão sem recorrer a dados de demonstração.

## Contrato esperado

| Método | Caminho | Corpo / resposta |
|---|---|---|
| `GET` | `/usuarios` | Lista de usuários: `[{ "id": 1, "nome": "Ana", "idade": 28, "peso": 68.5, "altura": 172, "objetivo": "GANHAR_MASSA" }]` |
| `GET` | `/treinos/predefinidos` | Lista treinos por grupo muscular, incluindo nome dos exercícios, séries e repetições |
| `POST` | `/usuarios` | Corpo com `nome`, `idade`, `peso`, `altura` e `objetivo`; resposta com o usuário criado |
| `DELETE` | `/usuarios/{id}` | Remove o aluno e seus exercícios associados; responde com `204 No Content` ou `404` se não existir |
| `GET` | `/usuarios/{id}/exercicios` | Lista de exercícios do usuário |
| `POST` | `/usuarios/{id}/exercicios` | Força: `tipo`, `nome`, `grupoMuscular`, `series`, `repeticoes`. Cardio: `tipo`, `nome`, `duracaoMinutos` |
| `DELETE` | `/exercicios/{id}` | Remove um exercício; pode responder com `204 No Content` |

Exercícios devem incluir `id`, `tipo` (`FORCA` ou `CARDIO`) e `nome`; os campos específicos seguem o corpo de criação. Os endpoints são implementados em `Principal/src/Service/ApiServer.java` e usam os repositórios JDBC existentes.
