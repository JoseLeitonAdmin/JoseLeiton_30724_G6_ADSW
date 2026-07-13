package com.aligest.command;

/**
 * Interfaz base para el patrón Command.
 */
public interface Command {
    /**
     * Ejecuta la acción del comando.
     */
    void execute();

    /**
     * Revierte (deshace) la acción del comando.
     */
    void undo();
}
