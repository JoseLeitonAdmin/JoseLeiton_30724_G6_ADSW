package com.aligest.adapter;

/**
 * Interfaz objetivo (Target) en el patrón Adapter.
 * Representa la estructura de datos que requiere el módulo de exportación.
 */
public interface ExportTarget {
    /**
     * Obtiene la cabecera formateada de la exportación (ej: cabecera CSV).
     */
    String getFormattedHeader();

    /**
     * Obtiene el contenido formateado del reporte.
     */
    String getFormattedContent();
}
