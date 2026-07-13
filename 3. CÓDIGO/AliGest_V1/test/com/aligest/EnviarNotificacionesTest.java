package com.aligest;

import com.aligest.model.Notificacion;
import com.aligest.repository.DataMock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias para REQ004: Enviar Notificaciones (15 pruebas).
 */
public class EnviarNotificacionesTest {

    private Notificacion notificacion;

    @Before
    public void setUp() {
        notificacion = new Notificacion("success", "Pago registrado Casa 01", "Hace 10 segundos");
        DataMock.inicializarDatos();
    }

    @Test
    public void testNotificacionConstructor() {
        assertNotNull(notificacion);
    }

    @Test
    public void testNotificacionGetType() {
        assertEquals("success", notificacion.getType());
    }

    @Test
    public void testNotificacionSetType() {
        notificacion.setType("warning");
        assertEquals("warning", notificacion.getType());
    }

    @Test
    public void testNotificacionGetMsg() {
        assertEquals("Pago registrado Casa 01", notificacion.getMsg());
    }

    @Test
    public void testNotificacionSetMsg() {
        notificacion.setMsg("Pago rechazado");
        assertEquals("Pago rechazado", notificacion.getMsg());
    }

    @Test
    public void testNotificacionGetTime() {
        assertEquals("Hace 10 segundos", notificacion.getTime());
    }

    @Test
    public void testNotificacionSetTime() {
        notificacion.setTime("Hace 1 minuto");
        assertEquals("Hace 1 minuto", notificacion.getTime());
    }

    @Test
    public void testNotificacionTypeSuccess() {
        Notificacion n = new Notificacion("success", "Msg", "Time");
        assertEquals("success", n.getType());
    }

    @Test
    public void testNotificacionTypeWarning() {
        Notificacion n = new Notificacion("warning", "Msg", "Time");
        assertEquals("warning", n.getType());
    }

    @Test
    public void testNotificacionTypeInfo() {
        Notificacion n = new Notificacion("info", "Msg", "Time");
        assertEquals("info", n.getType());
    }

    @Test
    public void testNotificacionTimelineSize() {
        int initialSize = DataMock.getNotificaciones().size();
        DataMock.getNotificaciones().add(0, notificacion);
        assertEquals(initialSize + 1, DataMock.getNotificaciones().size());
    }

    @Test
    public void testNotificacionAPIContent() {
        Notificacion apiNotif = new Notificacion("success", "WhatsApp API: Enviado", "12:00");
        assertTrue(apiNotif.getMsg().contains("WhatsApp API"));
    }

    @Test
    public void testNotificacionTimelineOrder() {
        DataMock.getNotificaciones().add(0, notificacion);
        // Debe ser el primero del timeline
        assertEquals(notificacion, DataMock.getNotificaciones().get(0));
    }

    @Test
    public void testNotificacionNullValues() {
        Notificacion n = new Notificacion(null, null, null);
        assertNull(n.getType());
        assertNull(n.getMsg());
        assertNull(n.getTime());
    }

    @Test
    public void testNotificacionDataMockInitState() {
        assertNotNull(DataMock.getNotificaciones());
        assertFalse(DataMock.getNotificaciones().isEmpty());
    }
}
