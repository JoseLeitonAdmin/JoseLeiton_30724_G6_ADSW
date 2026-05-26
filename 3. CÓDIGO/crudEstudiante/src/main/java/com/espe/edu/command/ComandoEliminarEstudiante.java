package com.espe.edu.command;

import com.espe.edu.model.Resultado;
import com.espe.edu.repository.RepositorioEstudiante;

/**
 * PATRÓN COMMAND — Comando Concreto: Eliminar
 * ─────────────────────────────────────────────────────────────────
 * Encapsula la operación "Eliminar estudiante" (D de CRUD).
 *
 * Verifica existencia antes de borrar para retornar un resultado
 * significativo a quien invocó el comando.
 */
public class ComandoEliminarEstudiante implements IComando {

    private final RepositorioEstudiante repositorio;
    private final String id;

    public ComandoEliminarEstudiante(RepositorioEstudiante repositorio, String id) {
        this.repositorio = repositorio;
        this.id          = id;
    }

    /**
     * Verifica existencia y elimina el estudiante.
     */
    @Override
    public Resultado ejecutar() {
        if (!repositorio.existeId(id)) {
            return new Resultado(false, "No se encontró estudiante con ID: " + id);
        }
        repositorio.eliminar(id);
        return new Resultado(true, "Estudiante eliminado correctamente.");
    }
}
