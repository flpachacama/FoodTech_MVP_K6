package com.foodtech.domain.model;

public record Coordenada(double x, double y) {

	public double distanciaA(Coordenada otra) {
		if (otra == null) {
			throw new IllegalArgumentException("Coordenada destino no puede ser null");
		}
		double deltaLatKm = (otra.y() - this.y) * 111.0;
		double latMediaRad = Math.toRadians((this.y + otra.y()) / 2.0);
		double deltaLngKm = (otra.x() - this.x) * 111.0 * Math.cos(latMediaRad);
		return Math.sqrt(deltaLatKm * deltaLatKm + deltaLngKm * deltaLngKm);
	}
}
