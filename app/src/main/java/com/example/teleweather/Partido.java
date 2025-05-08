package com.example.teleweather;

public class Partido {
    private final String equipoLocal;
    private final String equipoVisitante;
    private final String liga;
    private final String estadio;
    private final String fecha;
    private final String estado;
    private final String nombrePartido; // Nuevo campo para el nombre del partido/match

    public Partido(String equipoLocal, String equipoVisitante, String liga, String estadio, String fecha, String estado, String nombrePartido) {
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.liga = liga;
        this.estadio = estadio;
        this.fecha = fecha;
        this.estado = estado;
        this.nombrePartido = nombrePartido;
    }

    // Constructor de compatibilidad para código existente
    public Partido(String equipoLocal, String equipoVisitante, String liga, String estadio, String fecha, String estado) {
        this(equipoLocal, equipoVisitante, liga, estadio, fecha, estado, null);
    }

    public String getEquipoLocal() {
        return equipoLocal;
    }

    public String getEquipoVisitante() {
        return equipoVisitante;
    }

    public String getLiga() {
        return liga;
    }

    public String getEstadio() {
        return estadio;
    }

    public String getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public String getNombrePartido() {
        return nombrePartido;
    }
}