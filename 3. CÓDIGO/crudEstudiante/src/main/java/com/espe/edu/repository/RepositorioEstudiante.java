package com.espe.edu.repository;

import com.espe.edu.model.Estudiante;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria para la entidad Estudiante.
 * Responsable exclusivo del almacenamiento y recuperación de datos (persistencia).
 * Se inicializa con datos de ejemplo para demostración.
 */
public class RepositorioEstudiante {

    // Lista interna que simula la base de datos
    private final List<Estudiante> lista = new ArrayList<>();

    /**
     * Constructor: carga datos iniciales de ejemplo.
     */
    public RepositorioEstudiante() {
        
    }

    /** Verifica si ya existe un estudiante con el ID dado. */
    public boolean existeId(String id) {
        return lista.stream().anyMatch(e -> e.getId().equals(id));
    }

    /** Persiste un nuevo estudiante en la lista. */
    public void guardar(Estudiante estudiante) {
        lista.add(estudiante);
    }

    /** Busca y retorna el estudiante con el ID indicado, o null si no existe. */
    public Estudiante buscarPorId(String id) {
        return lista.stream()
                    .filter(e -> e.getId().equals(id))
                    .findFirst()
                    .orElse(null);
    }

    /** Reemplaza el estudiante existente con los nuevos datos. */
    public void actualizar(Estudiante actualizado) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId().equals(actualizado.getId())) {
                lista.set(i, actualizado);
                return;
            }
        }
    }

    /** Elimina el estudiante con el ID indicado. */
    public void eliminar(String id) {
        lista.removeIf(e -> e.getId().equals(id));
    }

    /** Retorna una copia de la lista completa de estudiantes. */
    public List<Estudiante> listarTodos() {
        return new ArrayList<>(lista);
    }
}
