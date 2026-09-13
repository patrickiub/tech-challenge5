# Vaga Zero

Sistema de recuperação de vagas ociosas no SUS. Hackathon FIAP — Pós Arquitetura e Desenvolvimento Java (Fase 5).

## O problema

Cerca de 25% dos horários agendados no SUS são perdidos por falta do paciente. Hoje a vaga
cancelada simplesmente evapora: o profissional fica ocioso enquanto centenas de pessoas
esperam meses na fila.

## A solução

Dois motores acoplados:

1. **Scoring de risco de falta** — avalia cada agendamento futuro e classifica o risco de
   ausência, de forma **determinística e explicável** (motor de regras, não ML).
2. **Cascata de convites** — quando uma vaga é liberada (cancelamento ou risco alto sem
   confirmação), o sistema oferece a vaga sequencialmente aos candidatos elegíveis da fila
   até alguém aceitar.

## Restrições de escopo — LEIA ANTES DE SUGERIR QUALQUER COISA

Este é um MVP de hackathon com **um desenvolvedor** e **~26 horas de trabalho**.

**NÃO implementar, nem sugerir:**
- Front-end de qualquer tipo (o hackathon dispensa explicitamente)
- Integração real com SISREG, e-SUS, RNDS ou qualquer sistema do Ministério da Saúde
- Envio real de SMS, WhatsApp ou e-mail (o notificacao-service apenas simula e loga)
- Machine learning de verdade — o scoring é motor de regras com pesos configuráveis
- Microsserviços além dos dois deployables definidos abaixo
- Kubernetes, service mesh, API gateway, service discovery
- Cache distribuído, Elasticsearch, MongoDB ou qualquer infra além de Postgres + Kafka

**Regra geral:** na dúvida entre a solução completa e a solução que funciona e é
demonstrável, escolha a segunda. Prefira sempre a implementação mais simples que
satisfaça o requisito.

## Stack

- Java 21, Spring Boot 3.x
- PostgreSQL 16 + Flyway (migrations versionadas, nunca `ddl-auto: update`)
- Apache Kafka em modo KRaft, single node (sem Zookeeper)
- Spring Security + JWT
- Resilience4j
- springdoc-openapi (Swagger UI — será usado na gravação do vídeo)
- JUnit 5 + Mockito
- Docker Compose (`compose.yml`)

## Deployables

### 1. `vaga-zero-api`
Monolito modular com arquitetura hexagonal. Módulos com fronteiras explícitas:

- `identidade` — autenticação, JWT, perfis (`PACIENTE`, `GESTOR`)
- `agenda` — unidades, vagas, agendamentos; publica eventos
- `risco` — scoring de risco de falta
- `fila` — fila de espera e motor de cascata de convites
- `demo` — seed de dados e avanço de tempo (recursos de demonstração)

Cada módulo segue: `domain/` (entidades e regras, sem dependência de framework),
`application/` (casos de uso), `infrastructure/` (persistência, Kafka, REST).

Módulos se comunicam por eventos de domínio ou por interfaces de caso de uso —
nunca acessando o repositório de outro módulo diretamente.

### 2. `notificacao-service`
Serviço Spring Boot separado e minimalista. Recebe pedidos de notificação e apenas loga.

Existe por dois motivos:
- É o alvo do Resilience4j (circuit breaker, retry, timeout, fallback)
- Expõe `POST /admin/chaos/{on|off}` para ser derrubado ao vivo durante a gravação,
  provando visualmente que o circuito abre e o fallback funciona

## Modelo de domínio

- **Paciente** — id, nome, cns, telefone, latitude, longitude, dataNascimento
- **Unidade** — id, nome, latitude, longitude
- **Vaga** — id, unidade, especialidade, profissional, dataHora, status
  (`DISPONIVEL`, `RESERVADA`, `EM_CASCATA`, `OCUPADA`, `PERDIDA`)
- **Agendamento** — id, vaga, paciente, status
  (`AGENDADO`, `CONFIRMADO`, `CANCELADO`, `REALIZADO`, `FALTOU`), confirmadoEm
- **ItemFila** — id, paciente, especialidade, dataEntrada, prioridadeClinica (1 a 5),
  aceitaChamadoImediato (boolean), raioMaximoKm
- **Convite** — id, vaga, paciente, enviadoEm, expiraEm, status
  (`ENVIADO`, `ACEITO`, `RECUSADO`, `EXPIRADO`), ordemNaCascata
- **AvaliacaoRisco** — id, agendamento, score, classificacao, avaliadoEm,
  **fatores** (lista de {codigo, descricao, pontos})

O campo `fatores` é obrigatório e não deve ser omitido: a explicabilidade do score é
o diferencial do projeto e será demonstrada no vídeo.

## Regras de negócio já decididas

### Scoring de risco (motor de regras)

Score inicial 0. Aplicar:

| Fator | Pontos |
|---|---|
| Cada falta nos últimos 12 meses | +25 (teto de 50) |
| Distância paciente-unidade > 10 km | +15 |
| Marcação feita com mais de 30 dias de antecedência | +10 |
| Primeira consulta do paciente na especialidade | +10 |
| Paciente entre 18 e 30 anos | +5 |
| Paciente confirmou presença ativamente | -40 |

Classificação: `BAIXO` (0-29), `MEDIO` (30-59), `ALTO` (60+).

Os pesos ficam em `application.yml` sob `vagazero.scoring.*` — devem ser configuráveis
sem recompilar.

### Elegibilidade e ordenação da cascata

Candidatos elegíveis: itens da fila com a **mesma especialidade** da vaga,
`aceitaChamadoImediato = true`, e distância até a unidade **dentro do raioMaximoKm**
declarado pelo paciente.

Ordenação (nesta ordem exata):
1. `prioridadeClinica` decrescente
2. Tempo de espera na fila decrescente (mais antigo primeiro)
3. Distância crescente

### Fluxo da cascata

1. Agendamento cancelado, ou risco `ALTO` sem confirmação até D-1 → vaga liberada
2. Publica `vaga.liberada`
3. Módulo `fila` consome, monta a lista ordenada de candidatos, vaga vira `EM_CASCATA`
4. Cria convite para o primeiro candidato com TTL configurável, publica `convite.enviado`
5. Se aceito: vaga vira `OCUPADA`, novo agendamento criado, demais candidatos descartados,
   publica `vaga.preenchida`
6. Se recusado ou expirado: publica `convite.expirado`, convida o próximo
7. Se a lista se esgota ou a data da vaga chega: vaga vira `PERDIDA`

**TTL do convite deve ser configurável por variável de ambiente**
(`VAGAZERO_CONVITE_TTL_SEGUNDOS`). Em produção seriam 1800 segundos; durante a gravação
do vídeo será 20, para que a cascata inteira aconteça na frente da câmera.

### Tópicos Kafka

`agendamento.cancelado`, `vaga.liberada`, `risco.avaliado`, `convite.enviado`,
`convite.aceito`, `convite.expirado`, `vaga.preenchida`

Eventos carregam apenas identificadores e dados mínimos, nunca a entidade inteira.

### Resiliência

Toda chamada de `vaga-zero-api` para `notificacao-service` deve ter, nesta ordem:
timeout → retry → circuit breaker → fallback.

O fallback **nunca** perde o convite: registra o convite como enviado e enfileira o
aviso para reenvio posterior. A indisponibilidade da notificação não pode quebrar a cascata.

## Recursos de demonstração

Ficam no módulo `demo`, protegidos por perfil Spring `demo` (não ativos por padrão):

- `POST /demo/seed` — popula o cenário completo: 1 unidade, especialidade Oftalmologia,
  10 vagas, 30 pacientes na fila com perfis variados (diferentes prioridades, distâncias,
  históricos de falta e disponibilidade para chamado imediato)
- `POST /demo/avancar-dias/{n}` — avança o relógio da aplicação, permitindo demonstrar o
  job de D-2 num vídeo de 8 minutos
- `POST /demo/reset` — limpa e repopula

Para o avanço de tempo funcionar, **todo o código deve obter a data/hora atual através de
um `Clock` injetado**, nunca via `LocalDateTime.now()` direto.

## Testes

Foco em qualidade sobre cobertura. Testar bem:
- Motor de scoring (cada fator isolado e combinações)
- Ordenação e filtro de elegibilidade da cascata
- Máquina de estados do convite e da vaga

Não escrever testes de integração com Testcontainers — custo de tempo alto demais para
o prazo disponível.

## Convenções

- Código, nomes de classes e comentários em português (o domínio é o SUS brasileiro)
- Entidades JPA não saem da camada de infraestrutura; usar DTOs nos controllers
- Sem `@Autowired` em campo — injeção por construtor
- Toda exceção de negócio herda de `VagaZeroException` e é tratada em um
  `@RestControllerAdvice`
- Migrations Flyway numeradas: `V1__criar_tabelas_base.sql`, etc.

## Ambiente de desenvolvimento

Antes de subir a aplicação, verifique se a porta 8080 já está ocupada.
Pode haver uma instância rodando pela IDE do desenvolvedor. Nunca suba
uma segunda instância em paralelo — testar contra bytecode desatualizado
já causou diagnóstico incorreto neste projeto.

Durante o desenvolvimento a aplicação roda com
VAGAZERO_CONVITE_TTL_SEGUNDOS=20 para permitir demonstração da
expiração de convites.

## Cronograma

| Data | Entrega |
|---|---|
| 12/09 | Compose subindo, esqueleto hexagonal, JWT, CRUD básico, Swagger |
| 13/09 | Cascata completa funcionando ponta a ponta |
| 17/09 (noite) | Scoring, job D-2, recursos de demo |
| 19/09 | notificacao-service, Resilience4j, testes, diagramas, README |
| 20/09 | Collection, relatório, roteiros de vídeo. **Code freeze.** |

Prioridade se o tempo apertar: cascata funcionando > compose em um comando > collection >
scoring > relatório > resiliência.

## Controle de versão

Ao concluir cada etapa ou bloco funcional, faça um commit antes de seguir
para o próximo. Uma etapa, um commit.

Formato: Conventional Commits em português, imperativo, sem ponto final.
Prefixos: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`.

Exemplos:
- `feat: motor de cascata de convites com TTL configuravel`
- `feat: scoring de risco de falta explicavel`
- `test: cobertura do motor de scoring`

Nunca faça `git push` sem eu pedir explicitamente.
Nunca inclua atribuição de ferramenta ou co-autoria nas mensagens de commit.