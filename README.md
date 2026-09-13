# Vaga Zero

Sistema de recuperacao de vagas ociosas no SUS.

Hackathon FIAP - Pos-graduacao em Arquitetura e Desenvolvimento Java (Fase 5).

Cerca de 25% dos horarios agendados no SUS sao perdidos por falta do paciente.
Hoje essa vaga simplesmente evapora. O Vaga Zero preve quem tem risco de faltar
e, quando uma vaga cai, oferece automaticamente em cascata para quem esta na
fila e pode comparecer.

Em desenvolvimento.

## Como executar

### Pré-requisitos

- Docker e Docker Compose

### Subir tudo

```
docker compose up -d --build
```

Um único comando sobe Postgres, Kafka e a `vaga-zero-api`, nessa ordem, com
Flyway rodando as migrations automaticamente. Nenhuma variável de ambiente
precisa ser exportada antes — todas já vêm com valores padrão no
`compose.yml`, incluindo `VAGAZERO_CONVITE_TTL_SEGUNDOS=20` (para a cascata
de convites expirar rápido durante demonstração).

Acompanhe até todos os serviços ficarem `healthy`:

```
docker compose ps
```

### Logs

```
docker compose logs -f vaga-zero-api
```

### Swagger

http://localhost:8080/swagger-ui.html

### Credenciais padrão

| Perfil | Email | Senha |
|---|---|---|
| GESTOR | gestor@vagazero.com | gestor123 |
| PACIENTE | maria.silva@email.com | paciente123 |

Autentique em `POST /auth/login` para obter o token JWT.

### Começar do zero

```
docker compose down -v
docker compose up -d --build
```
