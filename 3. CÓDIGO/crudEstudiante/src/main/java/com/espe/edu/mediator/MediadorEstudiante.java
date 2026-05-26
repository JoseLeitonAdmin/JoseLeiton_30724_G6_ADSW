package com.espe.edu.mediator;

import com.espe.edu.command.ComandoActualizarEstudiante;
import com.espe.edu.command.ComandoAgregarEstudiante;
import com.espe.edu.command.ComandoEliminarEstudiante;
import com.espe.edu.command.IComando;
import com.espe.edu.iterator.IIterador;
import com.espe.edu.iterator.IteradorEstudiante;
import com.espe.edu.model.Estudiante;
import com.espe.edu.model.Resultado;
import com.espe.edu.repository.RepositorioEstudiante;

/**
 * PATRÓN MEDIATOR — Implementación Concreta
 * ─────────────────────────────────────────────────────────────────
 * Orquesta todas las operaciones CRUD de Estudiante.
 *
 * Responsabilidades:
 *   1. Validar los datos de entrada antes de crear el comando.
 *   2. Instanciar el Comando concreto apropiado (PATRÓN COMMAND).
 *   3. Ejecutar el comando y retornar el Resultado a la Vista.
 *   4. Proveer un IIterador sobre la colección (PATRÓN ITERATOR).
 *
 * La Vista nunca toca el Repositorio directamente; siempre pasa por aquí.
 */
public class MediadorEstudiante implements IMediadorEstudiante {

    // Repositorio compartido entre todos los comandos
    private final RepositorioEstudiante repositorio;

    public MediadorEstudiante() {
        this.repositorio = new RepositorioEstudiante();
    }

    // ──────────────────────────────────────────────────
    //  Validación centralizada de datos de entrada
    // ──────────────────────────────────────────────────

    /**
     * Valida que id, nombre y edad sean valores aceptables.
     * Al centralizar la validación aquí, ningún Comando necesita
     * repetir esta lógica.
     */
    private Resultado validar(String id, String nombre, int edad) {
        if (id == null || id.isBlank())
            return new Resultado(false, "El ID no puede estar vacío.");
        if (nombre == null || nombre.isBlank())
            return new Resultado(false, "El nombre no puede estar vacío.");
        if (edad <= 0)
            return new Resultado(false, "La edad debe ser un número positivo.");
        return new Resultado(true, "OK");
    }

    // ──────────────────────────────────────────────────
    //  Métodos del Mediador — crean y ejecutan Comandos
    // ──────────────────────────────────────────────────

    /**
     * CREAR (C de CRUD).
     * Valida → crea ComandoAgregarEstudiante → ejecuta.
     */
    @Override
    public Resultado agregar(String id, String nombre, int edad) {
        Resultado validacion = validar(id, nombre, edad);
        if (!validacion.isExito()) return validacion;

        // PATRÓN COMMAND: el comando encapsula la lógica de negocio de agregar
        IComando comando = new ComandoAgregarEstudiante(repositorio, id, nombre, edad);
        return comando.ejecutar();
    }

    /**
     * ACTUALIZAR (U de CRUD).
     * Valida → crea ComandoActualizarEstudiante → ejecuta.
     */
    @Override
    public Resultado actualizar(String id, String nombre, int edad) {
        Resultado validacion = validar(id, nombre, edad);
        if (!validacion.isExito()) return validacion;

        // PATRÓN COMMAND: el comando encapsula la lógica de actualización
        IComando comando = new ComandoActualizarEstudiante(repositorio, id, nombre, edad);
        return comando.ejecutar();
    }

    /**
     * ELIMINAR (D de CRUD).
     * Valida ID mínimo → crea ComandoEliminarEstudiante → ejecuta.
     */
    @Override
    public Resultado eliminar(String id) {
        if (id == null || id.isBlank())
            return new Resultado(false, "El ID no puede estar vacío.");

        // PATRÓN COMMAND: el comando encapsula la lógica de eliminación
        IComando comando = new ComandoEliminarEstudiante(repositorio, id);
        return comando.ejecutar();
    }

    /**
     * LISTAR (R de CRUD) — devuelve un Iterador.
     * PATRÓN ITERATOR: la Vista recorre la colección sin conocer ArrayList.
     */
    @Override
    public IIterador<Estudiante> obtenerIterador() {
        return new IteradorEstudiante(repositorio.listarTodos());
    }
}
