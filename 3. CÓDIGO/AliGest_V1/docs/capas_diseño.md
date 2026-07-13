# Capas de Diseño en AliGest

Este documento describe la estructura arquitectónica de la aplicación **AliGest** (Administración de Alícuotas para el Condominio "La Primavera"). La aplicación ha sido reestructurada bajo un patrón arquitectónico de **Tres Capas**, garantizando una clara separación de responsabilidades, alta mantenibilidad, escalabilidad y facilidad de pruebas.

---

## Estructura General de Tres Capas

La arquitectura se divide en tres niveles lógicos de abstracción, cada uno responsable de una parte específica del ciclo de vida de la información:

```
┌─────────────────────────────────────────────────────────┐
│              Capa de Presentación (UI)                  │
│  - Muestra la interfaz gráfica y captura eventos.       │
│  - Paquete: com.aligest.ui                              │
└────────────────────────────┬────────────────────────────┘
                             │ (Invoca operaciones)
                             ▼
┌─────────────────────────────────────────────────────────┐
│             Capa de Negocio / Dominio                   │
│  - Modela entidades y gestiona lógica de cálculo.       │
│  - Contiene las reglas del negocio y patrones Command.   │
│  - Paquetes: com.aligest.model, com.aligest.command     │
└────────────────────────────┬────────────────────────────┘
                             │ (Consulta y guarda datos)
                             ▼
┌─────────────────────────────────────────────────────────┐
│        Capa de Acceso a Datos / Persistencia            │
│  - Gestiona y simula la persistencia de información.    │
│  - Paquete: com.aligest.repository                      │
└─────────────────────────────────────────────────────────┘
```

---

## 1. Capa de Presentación (Presentation Layer)
Es la capa más externa de la aplicación, encargada de interactuar directamente con el administrador del condominio. Renderiza la interfaz de usuario en pantalla y envía las intenciones de este hacia la capa de negocio.

- **Paquete Asociado:** [com.aligest.ui](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/ui)
- **Clase Principal:**
  - [MainFrame.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/ui/MainFrame.java): Ventana principal implementada con la biblioteca Swing de Java. Contiene la interfaz gráfica para el Login, Dashboard Financiero, Directorio de Copropietarios, Validación de Pagos Pendientes con soporte para deshacer (Undo), Historial de Notificaciones de WhatsApp y la generación de Reportes.
- **Punto de Entrada:**
  - [Main.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/Main.java): Clase de arranque que configura la apariencia nativa del sistema operativo (`LookAndFeel`) y lanza la ejecución de [MainFrame.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/ui/MainFrame.java) dentro del hilo de despacho de eventos de Swing (`EDT`).

---

## 2. Capa de Negocio / Dominio (Business Logic & Domain Layer)
Es el núcleo intelectual de la aplicación. Define el modelo conceptual del dominio y encapsula todas las reglas operativas y lógicas de cálculo (tales como la aplicación de multas o recargos por mora en alícuotas).

- **Paquetes Asociados:**
  - [com.aligest.model](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/model)
  - [com.aligest.command](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/command)
  - [com.aligest.adapter](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/adapter)
- **Clases del Modelo (Dominio):**
  - [Copropietario.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/model/Copropietario.java): Modela los datos de una casa y su respectivo copropietario (alícuota %, estado de pago, contacto).
  - [PagoPendiente.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/model/PagoPendiente.java): Contiene los datos de un pago ingresado pendiente de validación. Incluye la lógica de negocio `getMontoFinal()` y `getRecargoMora()`, la cual calcula automáticamente un **12% de recargo** si el pago aplica para mora.
  - [Notificacion.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/model/Notificacion.java): Modela el log de comunicaciones enviado a los residentes (ej: notificaciones de WhatsApp).
- **Lógica de Comportamiento (Patrón Command):**
  - Encapsula las operaciones del negocio (como la aprobación de pagos) permitiendo su ejecución de manera aislada y controlando el ciclo de vida de las acciones para soportar la reversibilidad (Undo/Deshacer).
- **Lógica de Traducción (Patrón Adapter):**
  - Facilita la comunicación con subsistemas o módulos externos (exportación a archivos planos). Actúa como un traductor entre los objetos de dominio y los formatos esperados en la salida de datos.

---

## 3. Capa de Acceso a Datos / Persistencia (Data Access Layer)
Responsable de proveer, almacenar y gestionar el estado persistente de la información de la aplicación. En un entorno de producción, esta capa se conectaría a un motor de base de datos relacional (ej: MySQL/PostgreSQL) o no relacional mediante un ORM/Framework. En este proyecto, se simula mediante una base de datos en memoria (Mocking).

- **Paquete Asociado:** [com.aligest.repository](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/repository)
- **Clase Principal:**
  - [DataMock.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/repository/DataMock.java): Centraliza la información del sistema (listas de copropietarios, pagos y notificaciones) utilizando datos deterministas inicializados a partir de una semilla fija (`new Random(42)`). Provee métodos de acceso globales de lectura y escritura simulando un patrón Repository.

---

## Ventajas de esta Arquitectura en AliGest

1. **Desacoplamiento Estricto:** La interfaz de usuario ([MainFrame.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/ui/MainFrame.java)) no calcula los recargos por mora ni manipula directamente bases de datos; simplemente solicita cálculos al modelo de dominio y pide información al repositorio.
2. **Facilidad de Pruebas Unitarias:** Es posible probar la lógica de cálculo de mora en [PagoPendiente.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/model/PagoPendiente.java) o el comportamiento de los comandos en [AprobarPagoCommand.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/command/AprobarPagoCommand.java) sin necesidad de inicializar interfaces gráficas complejas o conexiones de red.
3. **Escalabilidad de la Persistencia:** Si en el futuro AliGest requiere migrar a una base de datos real con Hibernate o JDBC, el cambio se limitará únicamente al paquete [com.aligest.repository](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/src/com/aligest/repository), dejando intacta la capa visual y de negocio.
