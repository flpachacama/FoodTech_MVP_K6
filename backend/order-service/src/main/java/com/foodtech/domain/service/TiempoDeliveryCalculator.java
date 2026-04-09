package com.foodtech.domain.service;

import org.springframework.stereotype.Service;

@Service
public class TiempoDeliveryCalculator {

    private static final double KM_POR_GRADO = 111.0;
    private static final double VELOCIDAD_PROMEDIO_KMH = 20.0;

    public int calcularMinutos(Double origenX, Double origenY, Double destinoX, Double destinoY) {
        if (origenX == null || origenY == null || destinoX == null || destinoY == null) return 0;

        double deltaLatKm = (destinoY - origenY) * KM_POR_GRADO;
        double latMediaRad = Math.toRadians((origenY + destinoY) / 2.0);
        double deltaLngKm = (destinoX - origenX) * KM_POR_GRADO * Math.cos(latMediaRad);
        double distanciaKm = Math.sqrt(deltaLatKm * deltaLatKm + deltaLngKm * deltaLngKm);

        return (int) Math.round((distanciaKm / VELOCIDAD_PROMEDIO_KMH) * 60);
    }
}
