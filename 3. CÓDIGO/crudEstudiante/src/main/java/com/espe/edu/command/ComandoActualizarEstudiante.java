package com.espe.edu.command;

import com.espe.edu.model.Estudiante;
import com.espe.edu.model.Resultado;
import com.espe.edu.repository.RepositorioEstudiante;

/**
 * PATRÓN COMMAND — Comando Concreto: Actualizar
 * ─────────────────────────────────────────────────────────────────
 * Encapsula la operación "Actualizar estudiante" (U de CRUD).
 *
 * Busca el estudiante por ID y reemplaza nombre y edad con los nuevos
 * valores. Retorna error descriptivo si el estudiante no existe.
 */
public class ComandoActualizarEstudiante implements IComando {

    private final RepositorioEstudiante repositorio;
    private final String id;
    private final String nuevoNombre;
    private final int    nuevaEdad;

    public ComandoActualizarEstudiante(RepositorioEstudiante repositorio,
                                       String id, String nuevoNombre, int nuevaEdad) {
        this.repositorio  = repositorio;
        this.id           = id;
        this.nuevoNombre  = nuevoNombre;
        this.nuevaEdad    = nuevaEdad;
    }

    /**
     * Localiza al estudiante y aplica los cambios.
     */
    @Override
    public Resultado ejecutar() {
        Estudiante existente = repositorio.buscarPorId(id);

        if (existente == null) {
            return new Resultado(false, "No se encontró estudiante con ID: " + id);
        }

        // Mutar y persistir
        existente.setNombre(nuevoNombre);
        existente.setEdad(nuevaEdad);
        repositorio.actualizar(existente);

        return new Resultado(true, "Estudiante actualizado correctamente.");
    }
}
