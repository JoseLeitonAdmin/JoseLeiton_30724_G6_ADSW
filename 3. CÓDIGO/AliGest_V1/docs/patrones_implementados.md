# Patrones de Diseño Implementados en AliGest

Este documento detalla conceptualmente los patrones de diseño orientados a objetos (patrones GoF - Gang of Four) incorporados en la arquitectura de **AliGest**. La implementación de estos patrones responde a necesidades específicas del negocio y mejora la robustez y flexibilidad del sistema.

---

## 1. Patrón Command (Comportamiento)

### Definición Conceptual
El patrón **Command** convierte una petición o solicitud en un objeto independiente que contiene toda la información necesaria para ejecutar dicha acción. Esta transformación permite parametrizar a los clientes con diferentes solicitudes, encolar o registrar solicitudes en un historial, y soportar operaciones que se pueden deshacer (Undo).

### Motivación en AliGest
En la administración del condominio, cuando el administrador decide **aprobar un pago pendiente**, se realiza una alteración directa sobre los registros financieros del sistema (el pago pendiente se elimina de la lista de pendientes y se procesa). Si el administrador comete un error, revertir esta transacción manualmente representaría una molestia operativa y un riesgo de inconsistencia de datos.

El patrón Command resuelve esto al encapsular la operación de aprobación de pago en un objeto comando que sabe:
1. Cómo ejecutarse (`execute()`).
2. Cómo revertirse exactamente al estado previo (`undo()`), recordando la posición original del registro y sus datos exactos.

### Componentes del Patrón en AliGest
- **Command (Interfaz):** [Command.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/command/Command.java) declara los métodos abstractos `execute()` y `undo()`.
- **ConcreteCommand (Comando Concreto):** [AprobarPagoCommand.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/command/AprobarPagoCommand.java) implementa la interfaz `Command`. Contiene la referencia al pago afectado, la lista de pagos de donde se remueve e inserta, y la lógica de callbacks para actualizar la interfaz de usuario en tiempo real.
- **Invoker (Invocador):** [HistorialComandos.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/command/HistorialComandos.java) mantiene una pila (`Stack`) de comandos ejecutados. Permite disparar la ejecución y revertir el último comando apilado llamando a su método `undo()`.
- **Client (Cliente):** [MainFrame.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/ui/MainFrame.java) instancia los comandos específicos con los datos seleccionados por el usuario y los envía al invocador para su ejecución.

---

## 2. Patrón Adapter (Estructural)

### Definición Conceptual
El patrón **Adapter** permite que dos clases con interfaces incompatibles puedan trabajar de manera conjunta. Actúa como un traductor intermedio (adaptador) que recibe peticiones de un cliente bajo una interfaz estándar y las traduce a un formato o llamada que una clase preexistente (el adaptado) puede entender.

### Motivación en AliGest
La aplicación requiere exportar reportes financieros en formato CSV. El módulo o flujo de exportación de archivos en la UI está diseñado para trabajar con una interfaz genérica llamada [ExportTarget.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/adapter/ExportTarget.java), la cual exige métodos estándar para obtener la cabecera y el contenido formateado de cualquier reporte.

Por otro lado, la información de los residentes se encuentra estructurada como una lista de objetos de tipo [Copropietario.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/model/Copropietario.java). Forzar a la clase [Copropietario.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/model/Copropietario.java) a implementar métodos de generación de cadenas CSV acoplaría el modelo de dominio a un formato de salida específico (violando el principio de responsabilidad única).

El patrón Adapter soluciona esto introduciendo una clase adaptadora que envuelve la lista de copropietarios y la expone como un `ExportTarget` compatible con el escritor de archivos.

### Componentes del Patrón en AliGest
- **Target (Interfaz Objetivo):** [ExportTarget.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/adapter/ExportTarget.java) define la interfaz esperada por el cliente de exportación (`getFormattedHeader()` y `getFormattedContent()`).
- **Adapter (Adaptador):** [CopropietarioCSVAdapter.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/adapter/CopropietarioCSVAdapter.java) implementa `ExportTarget`. Traduce las solicitudes a llamadas sobre la colección de copropietarios y formatea los datos.
- **Adaptee (Clase Adaptada):** La colección `List<Copropietario>` y la entidad [Copropietario.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/model/Copropietario.java) que contienen los datos nativos a exportar.
- **Client (Cliente):** El método `exportarReporteCSV()` en [MainFrame.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/ui/MainFrame.java), que interactúa únicamente con la abstracción `ExportTarget` sin conocer los detalles de cómo se construye la cadena CSV.
