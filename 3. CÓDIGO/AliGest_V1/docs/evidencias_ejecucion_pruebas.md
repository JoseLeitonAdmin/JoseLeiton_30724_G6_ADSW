# Evidencias de Ejecución de Pruebas Unitarias - AliGest

Este documento contiene las evidencias de ejecución y resultados de las pruebas unitarias del sistema **AliGest**, de acuerdo con lo definido en el [informe_plan_pruebas.md](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/docs/informe_plan_pruebas.md).

---

## 1. Detalles del Entorno de Ejecución

* **Sistema Operativo:** Windows 11
* **Versión de Java (JDK):** `23.0.1`
* **Framework de Pruebas:** JUnit 4.13.2 (con Hamcrest Core 1.3)
* **Fecha y Hora de la Ejecución:** 13 de Julio de 2026, 17:39:28 (Hora Local)
* **Script de Ejecución Usado:** [run_tests.bat](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/run_tests.bat)

---

## 2. Evidencia de la Consola (Salida Directa)

A continuación se presenta la transcripción literal de la salida obtenida al ejecutar la suite de pruebas unitarias desde la terminal mediante el script `run_tests.bat nopause`:

```text
===================================================
  Compilando AliGest y Pruebas Unitarias...
===================================================
Note: src\com\aligest\ui\MainFrame.java uses or overrides a deprecated API.
Note: Recompile with -Xlint:deprecation for details.

===================================================
  Ejecutando Pruebas Unitarias con JUnit...
===================================================
JUnit version 4.13.2
..................E.........................E................................
Time: 0,839
There were 2 failures:
1) testCopropietarioSetCorreo(com.aligest.GestionarCopropietariosTest)
java.lang.AssertionError: El correo debe ser válido y contener '@'
	at org.junit.Assert.fail(Assert.java:89)
	at org.junit.Assert.assertTrue(Assert.java:42)
	at com.aligest.GestionarCopropietariosTest.testCopropietarioSetCorreo(GestionarCopropietariosTest.java:115)
2) testPagoPendienteSetMonto(com.aligest.ImplementarPagosTest)
java.lang.AssertionError: El monto no debe ser negativo
	at org.junit.Assert.fail(Assert.java:89)
	at org.junit.Assert.assertTrue(Assert.java:42)
	at com.aligest.ImplementarPagosTest.testPagoPendienteSetMonto(ImplementarPagosTest.java:84)

FAILURES!!!
Tests run: 75,  Failures: 2
```

---

## 3. Resumen y Distribución de Resultados

De un total de **75 pruebas unitarias** agrupadas en la suite [AllTestsSuite.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/test/com/aligest/AllTestsSuite.java), se obtuvieron los siguientes resultados:

| Módulo / Caso de Prueba | Archivo de Prueba | Tests Totales | Aprobados | Fallidos | Tasa de Éxito | Estado |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: |
| **RF01: Administrar Sistema** | [AdministrarSistemaTest.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/test/com/aligest/AdministrarSistemaTest.java) | 15 | 15 | 0 | 100.0% | **PASA** |
| **RF02: Gestionar Copropietarios** | [GestionarCopropietariosTest.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/test/com/aligest/GestionarCopropietariosTest.java) | 20 | 19 | 1 | 95.0% | **ANOMALÍA** |
| **RF03: Implementar Pagos** | [ImplementarPagosTest.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/test/com/aligest/ImplementarPagosTest.java) | 25 | 24 | 1 | 96.0% | **ANOMALÍA** |
| **RF04: Enviar Notificaciones** | [EnviarNotificacionesTest.java](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/test/com/aligest/EnviarNotificacionesTest.java) | 15 | 15 | 0 | 100.0% | **PASA** |
| **TOTAL** | | **75** | **73** | **2** | **97.3%** | **APROBADO CON OBSERVACIONES** |

### Distribución Gráfica
```text
RF01: Administrar Sistema     | ██████████████ 15 tests (20.0%)
RF02: Gestionar Copropietarios | ███████████████████ 20 tests (26.7%)
RF03: Implementar Pagos        | ████████████████████████ 25 tests (33.3%)
RF04: Enviar Notificaciones    | ██████████████ 15 tests (20.0%)
```

---

## 4. Análisis de Fallos Detallados (Deuda Técnica Identificada)

Como se describe en el plan de pruebas, los dos fallos son **intencionales** e identifican carencias lógicas en la validación del modelo de datos:

### 1. Validación de Correo Electrónico (`GestionarCopropietariosTest.testCopropietarioSetCorreo`)
* **Ubicación del test:** [GestionarCopropietariosTest.java L112-L116](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/test/com/aligest/GestionarCopropietariosTest.java#L112-L116)
* **Mensaje de Error:** `java.lang.AssertionError: El correo debe ser válido y contener '@'`
* **Causa:** El método `setCorreo` en la entidad del modelo `Copropietario` no realiza ninguna validación de formato antes de asignar la cadena recibida, permitiendo direcciones sin `@`.
* **Acción Propuesta:** Implementar validación de formato mediante expresión regular en la clase del modelo.

### 2. Validación de Monto en Pago Pendiente (`ImplementarPagosTest.testPagoPendienteSetMonto`)
* **Ubicación del test:** [ImplementarPagosTest.java L81-L85](file:///c:/Users/Gabriel/Desktop/ADSW/SegundoParcial/AliGest/test/com/aligest/ImplementarPagosTest.java#L81-L85)
* **Mensaje de Error:** `java.lang.AssertionError: El monto no debe ser negativo`
* **Causa:** La entidad `PagoPendiente` permite la asignación de montos negativos a través de su setter, sin arrojar una excepción ni impedir la operación.
* **Acción Propuesta:** Agregar validación condicional en el setter `setMonto(...)` para asegurar que el monto sea mayor o igual a cero.

---

## 5. Conclusión de la Evaluación
Las pruebas se ejecutaron satisfactoriamente en **0.839 segundos** con cero pruebas intermitentes. 
Las pruebas unitarias cumplieron exitosamente su rol de control de calidad (QA), asegurando la correcto funcionamiento de la arquitectura de capas y el aislamiento de componentes, a la vez que documentan debilidades lógicas de dominio identificadas para su posterior resolución.
