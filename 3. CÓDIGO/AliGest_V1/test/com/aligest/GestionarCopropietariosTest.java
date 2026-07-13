package com.aligest;

import com.aligest.adapter.CopropietarioCSVAdapter;
import com.aligest.model.Copropietario;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Pruebas unitarias para REQ002: Gestionar Copropietarios (20 pruebas).
 */
public class GestionarCopropietariosTest {

    private Copropietario copropietario;

    @Before
    public void setUp() {
        copropietario = new Copropietario(101L, "1709876543", "Casa 05", "Díaz Stiven", 2.45, "Al Día", "0998765432", "stiven@correo.com");
    }

    @Test
    public void testCopropietarioConstructorValid() {
        assertNotNull(copropietario);
    }

    @Test
    public void testCopropietarioGetId() {
        assertEquals(101L, copropietario.getId());
    }

    @Test
    public void testCopropietarioSetId() {
        copropietario.setId(202L);
        assertEquals(202L, copropietario.getId());
    }

    @Test
    public void testCopropietarioGetCedula() {
        assertEquals("1709876543", copropietario.getCedula());
    }

    @Test
    public void testCopropietarioSetCedula() {
        copropietario.setCedula("1711111111");
        assertEquals("1711111111", copropietario.getCedula());
    }

    @Test
    public void testCopropietarioGetCasa() {
        assertEquals("Casa 05", copropietario.getCasa());
    }

    @Test
    public void testCopropietarioSetCasa() {
        copropietario.setCasa("Casa 10");
        assertEquals("Casa 10", copropietario.getCasa());
    }

    @Test
    public void testCopropietarioGetNombre() {
        assertEquals("Díaz Stiven", copropietario.getNombre());
    }

    @Test
    public void testCopropietarioSetNombre() {
        copropietario.setNombre("Leitón José");
        assertEquals("Leitón José", copropietario.getNombre());
    }

    @Test
    public void testCopropietarioGetAlicuota() {
        assertEquals(2.45, copropietario.getAlicuota(), 0.001);
    }

    @Test
    public void testCopropietarioSetAlicuota() {
        copropietario.setAlicuota(3.15);
        assertEquals(3.15, copropietario.getAlicuota(), 0.001);
    }

    @Test
    public void testCopropietarioGetEstado() {
        assertEquals("Al Día", copropietario.getEstado());
    }

    @Test
    public void testCopropietarioSetEstado() {
        copropietario.setEstado("En Mora");
        assertEquals("En Mora", copropietario.getEstado());
    }

    @Test
    public void testCopropietarioGetTelefono() {
        assertEquals("0998765432", copropietario.getTelefono());
    }

    @Test
    public void testCopropietarioSetTelefono() {
        copropietario.setTelefono("0990001112");
        assertEquals("0990001112", copropietario.getTelefono());
    }

    @Test
    public void testCopropietarioGetCorreo() {
        assertEquals("stiven@correo.com", copropietario.getCorreo());
    }

    @Test
    public void testCopropietarioSetCorreo() {
        // Validación estricta de correo (Este test fallará temporalmente porque el modelo no rechaza correos sin '@')
        copropietario.setCorreo("jose_sin_arroba.com");
        assertTrue("El correo debe ser válido y contener '@'", copropietario.getCorreo().contains("@"));
    }

    @Test
    public void testCopropietarioCSVAdapterHeader() {
        List<Copropietario> lista = new ArrayList<>();
        CopropietarioCSVAdapter adapter = new CopropietarioCSVAdapter(lista);
        String expectedHeader = "ID,Cedula,Casa,Copropietario,Alicuota (%),Estado Actual,Telefono,Correo\n";
        assertEquals(expectedHeader, adapter.getFormattedHeader());
    }

    @Test
    public void testCopropietarioCSVAdapterEmptyContent() {
        List<Copropietario> lista = new ArrayList<>();
        CopropietarioCSVAdapter adapter = new CopropietarioCSVAdapter(lista);
        assertEquals("", adapter.getFormattedContent());
    }

    @Test
    public void testCopropietarioCSVAdapterSanitizationAndFormatting() {
        List<Copropietario> lista = new ArrayList<>();
        lista.add(new Copropietario(1L, "1711111111", "Casa 01", "Manosalvas, Gabriel", 2.506, "Al Día", "0911111111", "gabriel@correo.com"));
        
        CopropietarioCSVAdapter adapter = new CopropietarioCSVAdapter(lista);
        String content = adapter.getFormattedContent();

        // Verificar remoción de comas en el nombre
        assertFalse(content.contains("Manosalvas,"));
        assertTrue(content.contains("Manosalvas Gabriel"));
        
        // Verificar punto decimal y redondeo a 2 cifras
        assertTrue(content.contains("2.51"));
        
        // Verificar el salto de línea
        assertTrue(content.endsWith("\n"));
    }
}
