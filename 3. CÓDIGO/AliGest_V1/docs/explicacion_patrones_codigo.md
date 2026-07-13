# Explicación de los Patrones en el Código

Este documento detalla técnicamente cómo se implementan los patrones de diseño **Command** y **Adapter** en el código de **AliGest**, analizando clases, métodos clave e interacciones del flujo de datos.

---

## 1. Patrón Command: Implementación Técnica

El patrón Command en AliGest se encarga de aislar la acción de **Aprobación de Pagos** y dar soporte a la característica de **Deshacer (Undo)**.

### A. La Interfaz de Comando
Ubicada en [Command.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/command/Command.java), establece el contrato para cualquier comando del sistema:

```java
package com.aligest.command;

public interface Command {
    void execute(); // Ejecuta la acción
    void undo();    // Revierte la acción
}
```

### B. El Comando Concreto
[AprobarPagoCommand.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/command/AprobarPagoCommand.java) implementa la lógica específica para procesar y revertir la aprobación de un pago pendiente. Mantiene variables internas para respaldar el estado antes de la ejecución:

```java
public class AprobarPagoCommand implements Command {
    private final long idPago;
    private final List<PagoPendiente> pagosPendientes;
    private PagoPendiente pagoRespaldado; // Respaldo del objeto eliminado
    private int posicionOriginal = -1;    // Respaldo de la posición en la lista
    private final Runnable updateUI;       // Callback para refrescar la vista

    public AprobarPagoCommand(long idPago, List<PagoPendiente> pagosPendientes, Runnable updateUI) {
        this.idPago = idPago;
        this.pagosPendientes = pagosPendientes;
        this.updateUI = updateUI;
    }

    @Override
    public void execute() {
        posicionOriginal = -1;
        // Buscar el pago y su índice
        for (int i = 0; i < pagosPendientes.size(); i++) {
            if (pagosPendientes.get(i).getId() == idPago) {
                posicionOriginal = i;
                break;
            }
        }
        // Ejecución: Remueve el pago y notifica a la UI
        if (posicionOriginal != -1) {
            pagoRespaldado = pagosPendientes.remove(posicionOriginal);
            if (updateUI != null) {
                updateUI.run();
            }
        }
    }

    @Override
    public void undo() {
        // Reversión: Reinserta el pago en su índice original y notifica a la UI
        if (pagoRespaldado != null && posicionOriginal != -1) {
            pagosPendientes.add(posicionOriginal, pagoRespaldado);
            if (updateUI != null) {
                updateUI.run();
            }
        }
    }
}
```

### C. El Invocador / Historial
[HistorialComandos.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/command/HistorialComandos.java) gestiona una pila de comandos (`Stack<Command>`) para controlar la secuencia de acciones revertibles:

```java
public class HistorialComandos {
    private final Stack<Command> pilaContenedora = new Stack<>();

    public void ejecutar(Command comando) {
        comando.execute();              // Ejecuta el comando
        pilaContenedora.push(comando);  // Lo almacena en el historial
    }

    public boolean deshacer() {
        if (pilaContenedora.isEmpty()) {
            return false;
        }
        Command ultimoComando = pilaContenedora.pop(); // Obtiene el último comando
        ultimoComando.undo();                          // Ejecuta su reversión
        return true;
    }
}
```

### D. Integración en la Interfaz Gráfica (Cliente)
En [MainFrame.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/ui/MainFrame.java), cuando el usuario pulsa el botón "Aprobar Pago", se crea y ejecuta el comando. Si el usuario hace clic en el botón flotante "Deshacer", se revierte el comando:

```java
// Acción del botón "Aprobar Pago"
btnAprobar.addActionListener(e -> {
    Command cmd = new AprobarPagoCommand(selectedPago.getId(), DataMock.getPagosPendientes(), () -> {
        llenarTablaPagos();
        actualizarMetricas();
    });

    historial.ejecutar(cmd); // Ejecución a través del historial
    dlg.dispose();

    generarComprobanteFisico(selectedPago);

    // Muestra notificación con la opción de Deshacer
    showToastWithUndo("Pago aprobado.", "success", () -> {
        historial.deshacer(); // Deshacer a través del historial
        eliminarComprobanteFisico(selectedPago.getId());
        showToast("Acción revertida. El pago vuelve a estar pendiente.", "info");
    });
});
```

---

## 2. Patrón Adapter: Implementación Técnica

El patrón Adapter en AliGest resuelve la incompatibilidad entre la interfaz de exportación genérica y la lista de copropietarios del condominio.

### A. La Interfaz Target
Ubicada en [ExportTarget.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/adapter/ExportTarget.java), define el contrato que requiere el módulo de exportación de archivos:

```java
package com.aligest.adapter;

public interface ExportTarget {
    String getFormattedHeader();  // Obtiene cabecera formateada (ej. Columnas CSV)
    String getFormattedContent(); // Obtiene contenido formateado en renglones
}
```

### B. El Adaptador
[CopropietarioCSVAdapter.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/adapter/CopropietarioCSVAdapter.java) implementa la interfaz `ExportTarget`. En su constructor recibe la lista de copropietarios (`List<Copropietario>`) y se encarga de convertir cada objeto a formato separado por comas (CSV), limpiando los datos de caracteres especiales:

```java
public class CopropietarioCSVAdapter implements ExportTarget {
    private final List<Copropietario> copropietarios;

    public CopropietarioCSVAdapter(List<Copropietario> copropietarios) {
        this.copropietarios = copropietarios;
    }

    @Override
    public String getFormattedHeader() {
        // Cabecera estándar CSV
        return "ID,Casa,Copropietario,Alicuota (%),Estado Actual,Telefono,Correo\n";
    }

    @Override
    public String getFormattedContent() {
        StringBuilder sb = new StringBuilder();
        for (Copropietario c : copropietarios) {
            // Lógica de traducción/limpieza: Limpiar comas del nombre del copropietario
            String nombreLimpio = c.getNombre().replace(",", "");
            
            sb.append(c.getId()).append(",")
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
```

### C. Consumo en la Interfaz Gráfica (Cliente)
En [MainFrame.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/ui/MainFrame.java), el cliente de exportación interactúa únicamente con la abstracción `ExportTarget`. De esta forma, si mañana se requiere exportar a formato JSON, XML o Excel, solo se creará un nuevo adaptador que implemente `ExportTarget`, sin modificar una sola línea del código de escritura de archivos en la UI:

```java
private void exportarReporteCSV() {
    showToast("Generando reporte financiero...", "info");

    Timer exportTimer = new Timer(800, e -> {
        try {
            // Uso del adaptador. La UI se acopla a ExportTarget, no al adaptador concreto
            ExportTarget adapter = new CopropietarioCSVAdapter(DataMock.getCopropietarios());

            File file = new File("AliGest_Reporte.csv");
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8))) {

                pw.print('\uFEFF'); // BOM de UTF-8
                pw.print(adapter.getFormattedHeader());  // Llama al Target
                pw.print(adapter.getFormattedContent()); // Llama al Target
            }
            showToast("Reporte descargado: AliGest_Reporte.csv", "success");
        } catch (Exception ex) {
            showToast("Error al exportar reporte.", "error");
            ex.printStackTrace();
        }
    });
    exportTimer.setRepeats(false);
    exportTimer.start();
}
```
