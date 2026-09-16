# Arquitetura — Vaga Zero

Diagramas fiéis à implementação atual do repositório (módulos, tópicos Kafka,
listeners e proteção de resiliência realmente existentes no código). Gerados
para o relatório e para a gravação do vídeo — priorizam clareza sobre
completude.

## 1. Componentes

Dois deployables: `vaga-zero-api` (monólito modular hexagonal, módulos
`identidade`, `agenda`, `risco`, `fila`, `demo`) e `notificacao-service`
(serviço separado, minimalista, só loga). `vaga-zero-api` persiste em
Postgres, publica e consome eventos em Kafka, e chama `notificacao-service`
via HTTP — essa chamada é o ponto de resiliência do projeto (timeout → retry
→ circuit breaker → fallback), destacado em vermelho. Os tópicos Kafka
`agendamento.cancelado`, `risco.avaliado`, `convite.aceito` e
`vaga.preenchida` são publicados para auditoria/observabilidade, mas hoje não
têm consumidor Java no repositório — só `vaga.liberada` (consumida pelo
módulo `fila` para iniciar a cascata) e `convite.enviado` (consumida para
disparar a notificação) têm `@KafkaListener`.

```mermaid
flowchart TB
    ator(["Paciente / Gestor"])

    subgraph API["vaga-zero-api (monolito modular hexagonal)"]
        direction TB
        identidade["identidade\nJWT, perfis"]
        agenda["agenda\nvagas, agendamentos"]
        risco["risco\nscoring de falta"]
        fila["fila\ncascata de convites"]
        demo["demo\nseed / relogio"]
    end

    kafka[("Kafka (KRaft)\nagendamento.cancelado\nvaga.liberada\nrisco.avaliado\nconvite.enviado\nconvite.aceito\nconvite.expirado\nvaga.preenchida")]
    postgres[("PostgreSQL")]
    notificacao["notificacao-service\n(loga e simula envio)"]

    ator -- "HTTP + JWT" --> identidade
    ator -- "HTTP + JWT" --> agenda
    ator -- "HTTP + JWT" --> fila

    agenda -- "publica agendamento.cancelado\npublica vaga.liberada" --> kafka
    risco -- "publica risco.avaliado" --> kafka
    kafka -- "consome vaga.liberada" --> fila
    fila -- "publica convite.enviado\nconvite.aceito\nconvite.expirado\nvaga.preenchida" --> kafka
    kafka -- "consome convite.enviado" --> fila

    identidade --- postgres
    agenda --- postgres
    risco --- postgres
    fila --- postgres
    demo --- postgres

    fila == "HTTP POST /notificacoes\ntimeout + retry + circuit breaker + fallback" ==> notificacao

    classDef resiliencia stroke:#c0392b,stroke-width:3px,color:#c0392b,font-weight:bold;
    class notificacao resiliencia;
    linkStyle 13 stroke:#c0392b,stroke-width:3px;
```

## 2. Sequência — cascata de convites (cancelamento até aceite)

Do cancelamento do agendamento até a ocupação da vaga. `AgendamentoService`
publica `agendamento.cancelado` e, na sequência, `vaga.liberada` — este
último é o gatilho consumido por `VagaLiberadaListener`, que aciona
`CascataService.iniciarCascata`. A transição de status da vaga
(`DISPONIVEL → EM_CASCATA`) e do convite (`ENVIADO → ACEITO`/`EXPIRADO`) usa
sempre um `UPDATE` atômico condicional, o que torna o consumo idempotente
mesmo com a semântica *at-least-once* do Kafka — por isso uma entrega
duplicada de `vaga.liberada` não abre uma segunda cascata. O envio da
notificação (destacado) roda em um listener separado (`convite.enviado`),
propositalmente desacoplado do motor da cascata: uma falha ali nunca
compromete a criação do convite, que já está persistido antes do listener
rodar.

```mermaid
sequenceDiagram
    actor G as Gestor
    participant AG as agenda (AgendamentoService)
    participant K as Kafka
    participant FI as fila (CascataService)
    participant EXP as ConviteExpiradorScheduler
    participant NOT as fila (ConviteEnviadoListener)
    participant NS as notificacao-service
    actor P as Paciente candidato

    G->>AG: POST /agendamentos/{id}/cancelar
    AG->>AG: status = CANCELADO
    AG->>K: publica agendamento.cancelado
    AG->>K: publica vaga.liberada
    K->>FI: consome vaga.liberada
    FI->>FI: vaga DISPONIVEL -> EM_CASCATA (update atomico)
    FI->>FI: seleciona proximo candidato elegivel (ordenado)
    FI->>K: publica convite.enviado (TTL configuravel)

    rect rgb(255, 235, 235)
        Note over NOT,NS: ponto de resiliencia - detalhe no diagrama 3
        K->>NOT: consome convite.enviado
        NOT->>NS: POST /notificacoes (timeout+retry+circuit breaker+fallback)
    end

    loop enquanto convite nao for aceito
        EXP->>FI: varredura periodica (scheduler-intervalo-ms)
        alt convite expirou (TTL vencido)
            FI->>FI: convite ENVIADO -> EXPIRADO (update atomico)
            FI->>K: publica convite.expirado
            FI->>FI: convida proximo candidato da fila
            FI->>K: publica convite.enviado
        else paciente aceita antes do TTL
            P->>FI: POST /convites/{id}/aceitar
            FI->>FI: convite ENVIADO -> ACEITO (update atomico)
            FI->>FI: vaga EM_CASCATA -> OCUPADA (update atomico)
            FI->>AG: cria agendamento CONFIRMADO
            FI->>FI: remove paciente da fila (ItemFila)
            FI->>K: publica convite.aceito
            FI->>K: publica vaga.preenchida
        end
    end
```

## 3. Sequência — resiliência na chamada ao notificacao-service

Toda chamada de `vaga-zero-api` a `notificacao-service` passa por
`NotificacaoClient`, protegido nesta ordem: **timeout** (connect/read
configurados no `RestClient`) → **retry** (`@Retry`, `max-attempts` +
`wait-duration`) → **circuit breaker** (`@CircuitBreaker`, janela contada por
chamadas) → **fallback** (`enviarFallback`). O `@Retry` envolve o
`@CircuitBreaker` (ordem configurada em `resilience4j.retry.retry-aspect-order`
/ `circuitbreaker.circuit-breaker-aspect-order`), então cada tentativa é
individualmente contabilizada pelo circuito. O fallback nunca perde o
convite — ele já está persistido como `ENVIADO` antes desta chamada — apenas
grava um `AvisoPendente` em Postgres para reenvio. `ReenvioAvisosScheduler`
varre os avisos pendentes periodicamente e chama `NotificacaoClient.reenviar`,
protegido pela mesma cadeia; quando o serviço volta (ou o circuito fecha), o
reenvio marca o aviso como `ENVIADO` sem intervenção manual.

```mermaid
sequenceDiagram
    participant L as ConviteEnviadoListener
    participant NC as NotificacaoClient
    participant NS as notificacao-service
    participant DB as Postgres (aviso_pendente)
    participant RE as ReenvioAvisosScheduler

    L->>NC: enviar(aviso)

    rect rgb(255, 235, 235)
        Note right of NC: RESILIENCIA: timeout -> retry -> circuit breaker -> fallback
        NC->>NS: POST /notificacoes (timeout-ms)
        alt sucesso
            NS-->>NC: 200 OK
        else timeout / erro / servico fora (chaos on)
            NS--xNC: falha
            NC->>NC: @Retry tenta novamente (max-attempts, wait-duration)
            NC->>NS: POST /notificacoes (retry)
            NS--xNC: falha novamente
            NC->>NC: @CircuitBreaker contabiliza falha na janela
            opt limiar de falha atingido
                NC->>NC: circuito abre (wait-duration-in-open-state)
            end
            NC->>NC: fallback: enviarFallback(aviso, causa)
            NC->>DB: salva AvisoPendente (status=PENDENTE)
        end
    end

    loop a cada reenvio-intervalo-ms
        RE->>DB: lista avisos PENDENTE
        RE->>NC: reenviar(pendente)
        rect rgb(255, 235, 235)
            Note right of NC: mesma cadeia timeout -> retry -> circuit breaker -> fallback
            NC->>NS: POST /notificacoes
            alt notificacao-service voltou / circuito fechado
                NS-->>NC: 200 OK
                NC->>DB: marca aviso ENVIADO
            else ainda indisponivel
                NS--xNC: falha
                NC->>NC: reenviarFallback incrementa tentativas
                NC->>DB: mantem PENDENTE
            end
        end
    end
```
