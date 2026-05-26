package com.espe.edu.command;

import com.espe.edu.model.Estudiante;
import com.espe.edu.model.Resultado;
import com.espe.edu.repository.RepositorioEstudiante;

/**
 * PATRÓN COMMAND — Comando Concreto: Agregar
 * ─────────────────────────────────────────────────────────────────
 * Encapsula la operación "Crear estudiante" (C de CRUD).
 *
 * Recibe todos los datos necesarios en el constructor, de modo que
 * cuando el Mediador llame a ejecutar(), el comando tiene todo lo que
 * necesita para completar la operación de forma autónoma.
 */
public class ComandoAgregarEstudiante implements IComando {

    private final RepositorioEstudiante repositorio;
    private final String id;
    private final String nombre;
    private final int    edad;

    public ComandoAgregarEstudiante(RepositorioEstudiante repositorio,
                                    String id, String nombre, int edad) {
        this.repositorio = repositorio;
        this.id          = id;
        this.nombre      = nombre;
        this.edad        = edad;
    }

    /**
     * Valida que no exista duplicado y persiste el nuevo estudiante.
     */
    @Override
    public Resultado ejecutar() {
        // Validar que el ID no esté en uso
        if (repositorio.existeId(id)) {
            return new Resultado(false, "Ya existe un estudiante con el ID: " + id);
        }
        // Crear y guardar la entidad
        repositorio.guardar(new Estudiante(id, nombre, edad));
        return new Resultado(true, "Estudiante agregado correctamente.");
    }
}
