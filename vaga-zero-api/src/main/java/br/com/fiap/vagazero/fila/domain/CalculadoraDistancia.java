package br.com.fiap.vagazero.fila.domain;

import java.math.BigDecimal;

/**
 * Distancia em linha reta entre duas coordenadas pela formula de Haversine.
 */
public final class CalculadoraDistancia {

    private static final double RAIO_TERRA_KM = 6371.0;

    private CalculadoraDistancia() {
    }

    public static double haversineKm(
            BigDecimal latitudeA, BigDecimal longitudeA, BigDecimal latitudeB, BigDecimal longitudeB) {
        double lat1 = Math.toRadians(latitudeA.doubleValue());
        double lat2 = Math.toRadians(latitudeB.doubleValue());
        double deltaLat = Math.toRadians(latitudeB.subtract(latitudeA).doubleValue());
        double deltaLon = Math.toRadians(longitudeB.subtract(longitudeA).doubleValue());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAIO_TERRA_KM * c;
    }
}
