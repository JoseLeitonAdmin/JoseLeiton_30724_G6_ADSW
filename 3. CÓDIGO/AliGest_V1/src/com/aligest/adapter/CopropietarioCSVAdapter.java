package com.aligest.adapter;

import com.aligest.model.Copropietario;
import java.util.List;

/**
 * Adaptador (Adapter) que traduce una lista de Copropietarios (Adaptee)
 * a la estructura requerida por ExportTarget (Target) para exportación a CSV.
 */
public class CopropietarioCSVAdapter implements ExportTarget {
    private final List<Copropietario> copropietarios;

    public CopropietarioCSVAdapter(List<Copropietario> copropietarios) {
        this.copropietarios = copropietarios;
    }

    @Override
    public String getFormattedHeader() {
        return "ID,Cedula,Casa,Copropietario,Alicuota (%),Estado Actual,Telefono,Correo\n";
    }

    @Override
    public String getFormattedContent() {
        StringBuilder sb = new StringBuilder();
        for (Copropietario c : copropietarios) {
            // Limpiar comas del nombre para evitar alterar las columnas del CSV
            String nombreLimpio = c.getNombre().replace(",", "");
            sb.append(c.getId()).append(",")
              .append(c.getCedula()).append(",")
              .append(c.getCasa()).append(",")
              .append(nombreLimpio).append(",")
              .append(String.format(java.util.Locale.US, "%.2f", c.getAlicuota())).append(",")
              .append(c.getEstado()).append(",")
              .append(c.getTelefono()).append(",")
              .append(c.getCorreo()).append("\n");
        }
        return sb.toString();
    }
}
