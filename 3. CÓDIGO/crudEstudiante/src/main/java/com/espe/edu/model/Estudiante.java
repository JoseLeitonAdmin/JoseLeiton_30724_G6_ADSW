package com.espe.edu.model;

/**
 * Entidad principal del sistema.
 * Representa un estudiante con sus datos básicos.
 */
public class Estudiante {

    private String id;
    private String nombre;
    private int edad;

    public Estudiante(String id, String nombre, int edad) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
    }

    public String getId()           { return id; }
    public String getNombre()       { return nombre; }
    public int getEdad()            { return edad; }

    public void setId(String id)            { this.id = id; }
    public void setNombre(String nombre)    { this.nombre = nombre; }
    public void setEdad(int edad)           { this.edad = edad; }

    @Override
    public String toString() {
        return "Estudiante{id='" + id + "', nombre='" + nombre + "', edad=" + edad + "}";
    }
}
