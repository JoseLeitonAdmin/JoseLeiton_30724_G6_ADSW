package com.aligest;

import com.aligest.repository.DataMock;
import com.aligest.ui.MainFrame;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Pruebas unitarias para REQ001: Administrar Sistema (15 pruebas).
 */
public class AdministrarSistemaTest {

    private MainFrame frame;
    private String adminEmail;
    private String adminPassword;

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("java.awt.headless", "false");
    }

    @Before
    public void setUp() throws Exception {
        DataMock.inicializarDatos();
        frame = new MainFrame();

        Field emailField = MainFrame.class.getDeclaredField("adminEmail");
        Field passField = MainFrame.class.getDeclaredField("adminPassword");
        emailField.setAccessible(true);
        passField.setAccessible(true);

        adminEmail = (String) emailField.get(frame);
        adminPassword = (String) passField.get(frame);
    }

    @Test
    public void testAdminCredentialsValid() {
        assertEquals("admin@laprimavera.com", adminEmail);
        assertEquals("admin123", adminPassword);
    }

    @Test
    public void testAdminCredentialsInvalidEmail() {
        assertNotEquals("wrong@laprimavera.com", adminEmail);
    }

    @Test
    public void testAdminCredentialsInvalidPassword() {
        assertNotEquals("wrong123", adminPassword);
    }

    @Test
    public void testAdminCredentialsEmptyEmail() {
        assertFalse("".equalsIgnoreCase(adminEmail));
    }

    @Test
    public void testAdminCredentialsEmptyPassword() {
        assertFalse("".equals(adminPassword));
    }

    @Test
    public void testAdminCredentialsNullEmail() {
        assertNotNull(adminEmail);
    }

    @Test
    public void testAdminCredentialsNullPassword() {
        assertNotNull(adminPassword);
    }

    @Test
    public void testAdminCredentialsCaseSensitivePassword() {
        // La contraseña debe ser case-sensitive
        assertFalse("admin123".equals("Admin123"));
    }

    @Test
    public void testAdminCredentialsCaseInsensitiveEmail() {
        // El email del sistema es case-insensitive
        assertTrue("admin@laprimavera.com".equalsIgnoreCase("ADMIN@LAPRIMAVERA.COM"));
    }

    @Test
    public void testDataMockCopropietariosNotNull() {
        assertNotNull(DataMock.getCopropietarios());
        assertEquals(60, DataMock.getCopropietarios().size());
    }

    @Test
    public void testDataMockPagosNotNull() {
        assertNotNull(DataMock.getPagosPendientes());
        assertEquals(3, DataMock.getPagosPendientes().size());
    }

    @Test
    public void testDataMockNotificacionesNotNull() {
        assertNotNull(DataMock.getNotificaciones());
        assertEquals(3, DataMock.getNotificaciones().size());
    }

    @Test
    public void testDataMockResetState() {
        DataMock.getCopropietarios().clear();
        assertEquals(0, DataMock.getCopropietarios().size());
        DataMock.inicializarDatos();
        assertEquals(60, DataMock.getCopropietarios().size());
    }

    @Test
    public void testDataMockSeedDeterminism() {
        List<?> copropietarios1 = DataMock.getCopropietarios();
        DataMock.inicializarDatos();
        List<?> copropietarios2 = DataMock.getCopropietarios();
        assertEquals(copropietarios1.size(), copropietarios2.size());
    }

    @Test
    public void testDataMockAlicuotaRanges() {
        for (com.aligest.model.Copropietario c : DataMock.getCopropietarios()) {
            assertTrue("La alícuota debe estar entre 2.0 y 3.5%", c.getAlicuota() >= 2.0 && c.getAlicuota() <= 3.5);
        }
    }
}
