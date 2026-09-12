package br.com.fiap.vagazero.demo.infrastructure;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Clock cujo instante pode ser adiantado em tempo de execucao, permitindo que os
 * recursos de demonstracao simulem a passagem de dias sem esperar o tempo real.
 */
public class RelogioAjustavel extends Clock {

    private final ZoneId zona;
    private final AtomicReference<Clock> delegado;

    public RelogioAjustavel(ZoneId zona) {
        this(zona, new AtomicReference<>(Clock.system(zona)));
    }

    private RelogioAjustavel(ZoneId zona, AtomicReference<Clock> delegado) {
        this.zona = zona;
        this.delegado = delegado;
    }

    public void avancar(Duration duracao) {
        delegado.set(Clock.offset(delegado.get(), duracao));
    }

    public void resetar() {
        delegado.set(Clock.system(zona));
    }

    @Override
    public ZoneId getZone() {
        return zona;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new RelogioAjustavel(zone, delegado);
    }

    @Override
    public Instant instant() {
        return delegado.get().instant();
    }
}
