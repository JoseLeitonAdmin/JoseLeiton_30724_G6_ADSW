# Diagrama de Arquitectura de 3 Capas

Este documento presenta el diagrama de arquitectura de la aplicación **AliGest** modelado bajo el patrón de **Tres Capas** y expresado en notación de bloques de **PlantUML**.

---

## Representación en PlantUML

Puedes copiar y pegar el siguiente código en cualquier herramienta de renderizado de PlantUML (como el servidor oficial de PlantUML o la extensión de VS Code) para visualizar el diagrama.

```plantuml
@startuml
skinparam BoxPadding 15
skinparam ParticipantPadding 15

title Arquitectura de 3 Capas - AliGest

' Definición de Estilos y Colores para cada capa
skinparam package {
    BackgroundColor<<Presentation>> #EBF8FF
    BorderColor<<Presentation>> #3182CE
    BackgroundColor<<Business>> #F0FFF4
    BorderColor<<Business>> #38A169
    BackgroundColor<<Data>> #FFFAF0
    BorderColor<<Data>> #DD6B20
}

package "Capa de Presentación (UI)" <<Presentation>> {
    class Main {
        + main(args: String[])
    }
    class MainFrame {
        - historial: HistorialComandos
        + MainFrame()
        - setupLoginView()
        - setupAppView()
        - exportarReporteCSV()
        - actualizarMetricas()
    }
    
    Main ..> MainFrame : "Instancia e inicia"
}

package "Capa de Negocio y Dominio" <<Business>> {
    class Copropietario {
        - id: long
        - casa: String
        - nombre: String
        - alicuota: double
        - estado: String
    }
    
    class PagoPendiente {
        - id: long
        - monto: double
        - mora: boolean
        + getMontoFinal(): double
        + getRecargoMora(): double
    }
    
    class Notificacion {
        - type: String
        - msg: String
    }
    
    package "Patrón Command" {
        interface Command {
            + execute()
            + undo()
        }
        class AprobarPagoCommand {
            - idPago: long
            - pagosPendientes: List<PagoPendiente>
            + execute()
            + undo()
        }
        class HistorialComandos {
            - pilaContenedora: Stack<Command>
            + ejecutar(comando: Command)
            + deshacer(): boolean
        }
        
        AprobarPagoCommand ..|> Command
        HistorialComandos "1" *--> "*" Command : "gestiona"
    }

    package "Patrón Adapter" {
        interface ExportTarget {
            + getFormattedHeader(): String
            + getFormattedContent(): String
        }
        class CopropietarioCSVAdapter {
            - copropietarios: List<Copropietario>
            + getFormattedHeader(): String
            + getFormattedContent(): String
        }
        
        CopropietarioCSVAdapter ..|> ExportTarget
        CopropietarioCSVAdapter "1" o--> "*" Copropietario : "adapta"
    }
}

package "Capa de Acceso a Datos / Persistencia" <<Data>> {
    class DataMock {
        - {static} copropietarios: List<Copropietario>
        - {static} pagosPendientes: List<PagoPendiente>
        - {static} notificaciones: List<Notificacion>
        + {static} inicializarDatos()
        + {static} getCopropietarios()
        + {static} getPagosPendientes()
    }
}

' Relaciones de dependencia y llamadas entre las capas
MainFrame ..> DataMock : "Consulta de datos e inicialización"
MainFrame ..> HistorialComandos : "Registra acciones de usuario"
MainFrame ..> AprobarPagoCommand : "Crea para ejecutar validación"
MainFrame ..> ExportTarget : "Usa para generación de reportes"
AprobarPagoCommand ..> DataMock : "Remueve/reinserta pagos de la BD"

@endum
```

---

## Explicación del Diagrama de Arquitectura

1. **Flujo Unidireccional y Desacoplado:**
   - La **Capa de Presentación** (`com.aligest.ui`) depende de la **Capa de Negocio** y de la **Capa de Datos** únicamente a través de interfaces y consultas de lectura/escritura seguras. La presentación jamás altera la lógica interna ni los datos directamente; solicita acciones.
2. **Ubicación de los Patrones:**
   - Los patrones **Command** y **Adapter** residen en la frontera de la **Capa de Negocio**, actuando como mediadores:
     - El **Command** intermedia entre el usuario de la UI que presiona "Aprobar" y el modelo financiero que debe removerse de la lista de pagos pendientes.
     - El **Adapter** intermedia entre la UI que requiere exportar texto estructurado (`ExportTarget`) y la colección interna de datos que maneja el negocio.
3. **Capa de Datos Simplificada:**
   - `DataMock` actúa como la base de datos en memoria, abstrayendo a las capas superiores de cómo se almacena o recupera la información.
