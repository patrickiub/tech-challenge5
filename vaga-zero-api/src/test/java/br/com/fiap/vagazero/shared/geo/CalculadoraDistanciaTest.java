package br.com.fiap.vagazero.shared.geo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CalculadoraDistanciaTest {

    @Test
    void distanciaEntreMesmoPontoEZero() {
        BigDecimal lat = new BigDecimal("-23.550520");
        BigDecimal lon = new BigDecimal("-46.633308");

        double distancia = CalculadoraDistancia.haversineKm(lat, lon, lat, lon);

        assertThat(distancia).isEqualTo(0.0, within(0.0001));
    }

    @Test
    void distanciaEntreSaoPauloERioDeJaneiroFicaPertoDosTrezentosNoventaKm() {
        BigDecimal latSp = new BigDecimal("-23.550520");
        BigDecimal lonSp = new BigDecimal("-46.633308");
        BigDecimal latRj = new BigDecimal("-22.906847");
        BigDecimal lonRj = new BigDecimal("-43.172897");

        double distancia = CalculadoraDistancia.haversineKm(latSp, lonSp, latRj, lonRj);

        assertThat(distancia).isCloseTo(357.0, within(10.0));
    }

    @Test
    void distanciaEhSimetrica() {
        BigDecimal latA = new BigDecimal("-23.550520");
        BigDecimal lonA = new BigDecimal("-46.633308");
        BigDecimal latB = new BigDecimal("-22.906847");
        BigDecimal lonB = new BigDecimal("-43.172897");

        double distanciaIda = CalculadoraDistancia.haversineKm(latA, lonA, latB, lonB);
        double distanciaVolta = CalculadoraDistancia.haversineKm(latB, lonB, latA, lonA);

        assertThat(distanciaIda).isEqualTo(distanciaVolta, within(0.0001));
    }
}
