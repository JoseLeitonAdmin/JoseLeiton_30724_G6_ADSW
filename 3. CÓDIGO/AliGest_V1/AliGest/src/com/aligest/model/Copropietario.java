package com.aligest.model;

/**
 * Representa a un copropietario de una casa en el condominio.
 */
public class Copropietario {
    private long id;
    private String casa;
    private String nombre;
    private double alicuota;
    private String estado; // "Al Día" o "En Mora"
    private String telefono;
    private String correo;

    public Copropietario(long id, String casa, String nombre, double alicuota, String estado, String telefono, String correo) {
        this.id = id;
        this.casa = casa;
        this.nombre = nombre;
        this.alicuota = alicuota;
        this.estado = estado;
        this.telefono = telefono;
        this.correo = correo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCasa() {
        return casa;
    }

    public void setCasa(String casa) {
        this.casa = casa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getAlicuota() {
        return alicuota;
    }

    public void setAlicuota(double alicuota) {
        this.alicuota = alicuota;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
