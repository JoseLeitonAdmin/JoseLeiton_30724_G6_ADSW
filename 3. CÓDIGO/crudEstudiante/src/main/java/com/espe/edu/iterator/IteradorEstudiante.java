package com.espe.edu.iterator;

import com.espe.edu.model.Estudiante;
import java.util.List;

/**
 * PATRÓN ITERATOR — Iterador Concreto: Estudiante
 * ─────────────────────────────────────────────────────────────────
 * Implementación concreta que recorre una List<Estudiante> de forma
 * secuencial usando el contrato definido por IIterador<Estudiante>.
 *
 * La Vista (FormularioCrudEstudiante) usa esta clase para poblar la
 * tabla sin manipular índices ni conocer la estructura interna de la
 * colección del Repositorio.
 */
public class IteradorEstudiante implements IIterador<Estudiante> {

    private final List<Estudiante> lista;
    // Puntero interno que avanza con cada llamada a siguiente()
    private int posicion = 0;

    /**
     * @param lista Lista de estudiantes a recorrer (no null).
     */
    public IteradorEstudiante(List<Estudiante> lista) {
        this.lista = lista;
    }

    /** Retorna true mientras el puntero no haya llegado al final. */
    @Override
    public boolean tieneSiguiente() {
        return posicion < lista.size();
    }

    /** Retorna el estudiante en la posición actual y avanza el puntero. */
    @Override
    public Estudiante siguiente() {
        return lista.get(posicion++);
    }
}
