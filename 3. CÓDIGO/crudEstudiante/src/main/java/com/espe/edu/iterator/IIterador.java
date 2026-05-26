package com.espe.edu.iterator;

/**
 * PATRÓN ITERATOR
 * ─────────────────────────────────────────────────────────────────
 * Interfaz genérica de iterador que define el contrato para recorrer
 * una colección de elementos sin exponer su estructura interna.
 *
 * Al parametrizarla con genéricos, puede reutilizarse con cualquier
 * tipo de entidad del sistema (Estudiante, Profesor, Curso, etc.).
 *
 * Beneficios en este proyecto:
 *   - La Vista itera los estudiantes sin conocer ArrayList ni índices.
 *   - Permite cambiar la estructura de datos interna (LinkedList, etc.)
 *     sin modificar la Vista.
 *   - Facilita crear iteradores con filtros (p.ej. solo mayores de 18).
 *
 * @param <T> Tipo de elemento sobre el que se itera.
 */
public interface IIterador<T> {

    /** @return true si aún quedan elementos por recorrer. */
    boolean tieneSiguiente();

    /** @return el siguiente elemento en la secuencia. */
    T siguiente();
}
