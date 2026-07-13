package com.aligest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Suite principal de pruebas unitarias de AliGest.
 * Agrupa todas las pruebas unitarias correspondientes a los Requisitos Funcionales.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    AdministrarSistemaTest.class,
    GestionarCopropietariosTest.class,
    ImplementarPagosTest.class,
    EnviarNotificacionesTest.class
})
public class AllTestsSuite {
}
