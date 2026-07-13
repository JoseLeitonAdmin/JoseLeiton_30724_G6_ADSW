package com.aligest.model;

/**
 * Representa un pago pendiente de validación en el sistema.
 */
public class PagoPendiente {
    private long id;
    private String fecha;
    private String casa;
    private String nombre;
    private double monto;
    private boolean mora; // si aplica recargo de mora del 12%
    private String expensa; // mes de la expensa, ej: "Abril 2026"

    public PagoPendiente(long id, String fecha, String casa, String nombre, double monto, boolean mora, String expensa) {
        this.id = id;
        this.fecha = fecha;
        this.casa = casa;
        this.nombre = nombre;
        this.monto = monto;
        this.mora = mora;
        this.expensa = expensa;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public boolean isMora() {
        return mora;
    }

    public void setMora(boolean mora) {
        this.mora = mora;
    }

    public String getExpensa() {
        return expensa;
    }

    public void setExpensa(String expensa) {
        this.expensa = expensa;
    }

    /**
     * Calcula el monto final considerando si aplica mora (12% de recargo).
     */
    public double getMontoFinal() {
        if (mora) {
            return Math.round((monto * 1.12) * 100.0) / 100.0;
        }
        return monto;
    }

    /**
     * Obtiene el recargo por mora aplicado.
     */
    public double getRecargoMora() {
        if (mora) {
            return Math.round((monto * 0.12) * 100.0) / 100.0;
        }
        return 0.0;
    }
}
