package com.espe.edu.command;

import com.espe.edu.model.Resultado;

/**
 * PATRÓN COMMAND
 * ─────────────────────────────────────────────────────────────────
 * Interfaz base para todos los comandos del sistema.
 *
 * Cada operación CRUD se encapsula como un objeto comando independiente.
 * Esto desacopla quien solicita la acción (Vista) de quien la ejecuta
 * (Repositorio), y permite extender operaciones sin modificar código existente.
 *
 * Beneficios en este proyecto:
 *   - La Vista solo conoce IComando, no el controlador ni el repositorio.
 *   - Facilita agregar Undo/Redo en el futuro.
 *   - Cada comando es testeable de forma aislada.
 */
public interface IComando {

    /**
     * Ejecuta la operación encapsulada en el comando.
     *
     * @return Resultado con indicador de éxito y mensaje descriptivo.
     */
    Resultado ejecutar();
}
