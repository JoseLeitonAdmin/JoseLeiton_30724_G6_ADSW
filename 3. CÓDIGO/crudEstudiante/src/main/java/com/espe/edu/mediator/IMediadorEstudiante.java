package com.espe.edu.mediator;

import com.espe.edu.iterator.IIterador;
import com.espe.edu.model.Estudiante;
import com.espe.edu.model.Resultado;

/**
 * PATRÓN MEDIATOR
 * ─────────────────────────────────────────────────────────────────
 * Interfaz del Mediador central del sistema.
 *
 * El Mediador coordina la comunicación entre la Vista y los Comandos/
 * Repositorio. Ningún componente se comunica directamente con otro;
 * todo pasa por aquí.
 *
 *   Vista  ──────►  IMediadorEstudiante  ──────►  Comandos  ──────►  Repositorio
 *
 * Beneficios en este proyecto:
 *   - La Vista solo depende de esta interfaz, no de implementaciones.
 *   - Agregar validaciones transversales (logging, auditoría) se hace
 *     una sola vez en el Mediador, no en cada comando ni en la Vista.
 *   - Facilita el testing: se puede inyectar un MediadorFake en los tests.
 */
public interface IMediadorEstudiante {

    /** Valida y delega la creación al ComandoAgregarEstudiante. */
    Resultado agregar(String id, String nombre, int edad);

    /** Valida y delega la actualización al ComandoActualizarEstudiante. */
    Resultado actualizar(String id, String nombre, int edad);

    /** Valida y delega la eliminación al ComandoEliminarEstudiante. */
    Resultado eliminar(String id);

    /**
     * Retorna un iterador para recorrer todos los estudiantes.
     * Integra el PATRÓN ITERATOR: la Vista itera sin conocer la List interna.
     */
    IIterador<Estudiante> obtenerIterador();
}
