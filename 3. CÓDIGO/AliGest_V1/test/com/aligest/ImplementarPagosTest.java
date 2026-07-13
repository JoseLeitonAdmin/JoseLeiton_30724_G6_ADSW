package com.aligest;

import com.aligest.command.AprobarPagoCommand;
import com.aligest.command.Command;
import com.aligest.command.HistorialComandos;
import com.aligest.model.PagoPendiente;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Pruebas unitarias para REQ003: Implementar Pagos (25 pruebas).
 */
public class ImplementarPagosTest {

    private PagoPendiente pago;

    @Before
    public void setUp() {
        pago = new PagoPendiente(501L, "09/07/2026", "Casa 12", "Manosalvas Gabriel", 50.00, false, "Junio 2026");
    }

    @Test
    public void testPagoPendienteConstructor() {
        assertNotNull(pago);
    }

    @Test
    public void testPagoPendienteGetId() {
        assertEquals(501L, pago.getId());
    }

    @Test
    public void testPagoPendienteSetId() {
        pago.setId(602L);
        assertEquals(602L, pago.getId());
    }

    @Test
    public void testPagoPendienteGetFecha() {
        assertEquals("09/07/2026", pago.getFecha());
    }

    @Test
    public void testPagoPendienteSetFecha() {
        pago.setFecha("10/07/2026");
        assertEquals("10/07/2026", pago.getFecha());
    }

    @Test
    public void testPagoPendienteGetCasa() {
        assertEquals("Casa 12", pago.getCasa());
    }

    @Test
    public void testPagoPendienteSetCasa() {
        pago.setCasa("Casa 15");
        assertEquals("Casa 15", pago.getCasa());
    }

    @Test
    public void testPagoPendienteGetNombre() {
        assertEquals("Manosalvas Gabriel", pago.getNombre());
    }

    @Test
    public void testPagoPendienteSetNombre() {
        pago.setNombre("Díaz Stiven");
        assertEquals("Díaz Stiven", pago.getNombre());
    }

    @Test
    public void testPagoPendienteGetMonto() {
        assertEquals(50.00, pago.getMonto(), 0.001);
    }

    @Test
    public void testPagoPendienteSetMonto() {
        // Validación de monto no negativo (Este test fallará temporalmente porque el modelo no valida números negativos en setMonto)
        pago.setMonto(-10.00);
        assertTrue("El monto no debe ser negativo", pago.getMonto() >= 0.0);
    }

    @Test
    public void testPagoPendienteIsMora() {
        assertFalse(pago.isMora());
    }

    @Test
    public void testPagoPendienteSetMora() {
        pago.setMora(true);
        assertTrue(pago.isMora());
    }

    @Test
    public void testPagoPendienteGetExpensa() {
        assertEquals("Junio 2026", pago.getExpensa());
    }

    @Test
    public void testPagoPendienteSetExpensa() {
        pago.setExpensa("Julio 2026");
        assertEquals("Julio 2026", pago.getExpensa());
    }

    @Test
    public void testPagoPendienteMontoFinalWithoutMora() {
        pago.setMora(false);
        assertEquals(50.00, pago.getMontoFinal(), 0.001);
    }

    @Test
    public void testPagoPendienteMontoFinalWithMora() {
        pago.setMora(true);
        // 50 * 1.12 = 56.00
        assertEquals(56.00, pago.getMontoFinal(), 0.001);
    }

    @Test
    public void testPagoPendienteRecargoMoraWithoutMora() {
        pago.setMora(false);
        assertEquals(0.00, pago.getRecargoMora(), 0.001);
    }

    @Test
    public void testPagoPendienteRecargoMoraWithMora() {
        pago.setMora(true);
        // 50 * 0.12 = 6.00
        assertEquals(6.00, pago.getRecargoMora(), 0.001);
    }

    @Test
    public void testAprobarPagoCommandExecution() {
        List<PagoPendiente> lista = new ArrayList<>();
        lista.add(pago);

        AprobarPagoCommand cmd = new AprobarPagoCommand(pago.getId(), lista, null);
        cmd.execute();

        assertTrue(lista.isEmpty());
        assertEquals(pago, cmd.getPagoRespaldado());
    }

    @Test
    public void testAprobarPagoCommandUndo() {
        List<PagoPendiente> lista = new ArrayList<>();
        lista.add(pago);

        AprobarPagoCommand cmd = new AprobarPagoCommand(pago.getId(), lista, null);
        cmd.execute();
        cmd.undo();

        assertEquals(1, lista.size());
        assertEquals(pago, lista.get(0));
    }

    @Test
    public void testAprobarPagoCommandNonExistentId() {
        List<PagoPendiente> lista = new ArrayList<>();
        lista.add(pago);

        AprobarPagoCommand cmd = new AprobarPagoCommand(999L, lista, null);
        cmd.execute();

        assertEquals(1, lista.size());
        assertNull(cmd.getPagoRespaldado());
    }

    @Test
    public void testHistorialComandosEjecutar() {
        List<PagoPendiente> lista = new ArrayList<>();
        lista.add(pago);

        HistorialComandos historial = new HistorialComandos();
        Command cmd = new AprobarPagoCommand(pago.getId(), lista, null);
        
        historial.ejecutar(cmd);
        assertTrue(lista.isEmpty());
    }

    @Test
    public void testHistorialComandosDeshacer() {
        List<PagoPendiente> lista = new ArrayList<>();
        lista.add(pago);

        HistorialComandos historial = new HistorialComandos();
        Command cmd = new AprobarPagoCommand(pago.getId(), lista, null);
        
        historial.ejecutar(cmd);
        assertTrue(historial.deshacer());
        assertEquals(pago, lista.get(0));
    }

    @Test
    public void testHistorialComandosLimpiar() {
        List<PagoPendiente> lista = new ArrayList<>();
        lista.add(pago);

        HistorialComandos historial = new HistorialComandos();
        Command cmd = new AprobarPagoCommand(pago.getId(), lista, null);
        
        historial.ejecutar(cmd);
        historial.limpiar();
        
        assertFalse(historial.deshacer());
    }
}
