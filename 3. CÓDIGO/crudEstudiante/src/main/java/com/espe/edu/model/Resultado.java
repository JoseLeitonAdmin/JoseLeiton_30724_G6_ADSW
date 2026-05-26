package com.espe.edu.model;

/**
 * Value Object que encapsula el resultado de una operación CRUD.
 * Contiene un indicador de éxito y un mensaje descriptivo.
 */
public class Resultado {

    private final boolean exito;
    private final String mensaje;

    public Resultado(boolean exito, String mensaje) {
        this.exito   = exito;
        this.mensaje = mensaje;
    }

    public boolean isExito()    { return exito; }
    public String getMensaje()  { return mensaje; }
}
