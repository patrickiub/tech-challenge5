/**
 * Integracao com o notificacao-service: cliente HTTP protegido por
 * Resilience4j (timeout, retry, circuit breaker, fallback) e o
 * reprocessamento dos avisos que caem no fallback.
 */
package br.com.fiap.vagazero.fila.infrastructure.notificacao;
