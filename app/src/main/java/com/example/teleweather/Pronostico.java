package com.example.teleweather;

public class Pronostico {
    private final String fecha;
    private final double temperaturaMaxima;
    private final double temperaturaMinima;
    private final String condicionClima;
    private final String iconoUrl;
    private final double humedad;
    private final double probabilidadLluvia;

    public Pronostico(String fecha, double temperaturaMaxima, double temperaturaMinima,
                      String condicionClima, String iconoUrl, double humedad, double probabilidadLluvia) {
        this.fecha = fecha;
        this.temperaturaMaxima = temperaturaMaxima;
        this.temperaturaMinima = temperaturaMinima;
        this.condicionClima = condicionClima;
        this.iconoUrl = iconoUrl;
        this.humedad = humedad;
        this.probabilidadLluvia = probabilidadLluvia;
    }

    public String getFecha() {
        return fecha;
    }

    public double getTemperaturaMaxima() {
        return temperaturaMaxima;
    }

    public double getTemperaturaMinima() {
        return temperaturaMinima;
    }

    public String getCondicionClima() {
        return condicionClima;
    }

    public String getIconoUrl() {
        return iconoUrl;
    }

    public double getHumedad() {
        return humedad;
    }

    public double getProbabilidadLluvia() {
        return probabilidadLluvia;
    }
}