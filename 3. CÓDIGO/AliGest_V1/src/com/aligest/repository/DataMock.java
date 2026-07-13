package com.aligest.repository;

import com.aligest.model.Copropietario;
import com.aligest.model.PagoPendiente;
import com.aligest.model.Notificacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Repositorio de datos en memoria para simular la base de datos de AliGest.
 * Utiliza una semilla fija para generar los datos de manera determinista.
 */
public class DataMock {
    private static List<Copropietario> copropietarios;
    private static List<PagoPendiente> pagosPendientes;
    private static List<Notificacion> notificaciones;

    static {
        inicializarDatos();
    }

    public static void inicializarDatos() {
        copropietarios = new ArrayList<>();
        pagosPendientes = new ArrayList<>();
        notificaciones = new ArrayList<>();

        // Semilla fija para mantener datos consistentes
        Random rand = new Random(42);
        String[] nombres = {
            "Gualotuña Brayan", "Santi Jeancarlo", "Obando Erick", 
            "Familia López", "María Fernanda Silva", "Carlos Zambrano", "Andrea Herrera"
        };

        for (int i = 0; i < 60; i++) {
            long id = i + 1;
            String cedula = String.format("17%08d", id);
            String casa = String.format("Casa %02d", id);
            String nombre = nombres[i % nombres.length];
            double alicuota = Math.round((2.0 + rand.nextDouble() * 1.5) * 100.0) / 100.0;
            
            // Aproximadamente el 20% están en mora (mismo ratio > 0.8 en JS)
            boolean isMora = rand.nextDouble() > 0.8;
            String estado = isMora ? "En Mora" : "Al Día";
            
            String telefono = String.format("09%d", 10000000 + rand.nextInt(90000000));
            String correo = "prop" + id + "@correo.com";

            copropietarios.add(new Copropietario(id, cedula, casa, nombre, alicuota, estado, telefono, correo));
        }

        // Agregar pagos pendientes iniciales
        pagosPendientes.add(new PagoPendiente(101, "12/04/2026", "Casa 05", "María Fernanda Silva", 22.00, false, "Abril 2026"));
        pagosPendientes.add(new PagoPendiente(102, "14/04/2026", "Casa 18", "Carlos Zambrano", 22.00, true, "Abril 2026"));
        pagosPendientes.add(new PagoPendiente(103, "14/04/2026", "Casa 42", "Andrea Herrera", 22.00, false, "Abril 2026"));

        // Agregar notificaciones iniciales del Timeline
        notificaciones.add(new Notificacion("success", "Pago Aprobado: Casa 12 (Marzo 2026). Comprobante PDF enviado.", "Hace 2 horas"));
        notificaciones.add(new Notificacion("warning", "Aviso de Mora (12%): Casa 18. Expensa no pagada en 5 días.", "Hace 5 horas"));
        notificaciones.add(new Notificacion("info", "Recordatorio Preventivo enviado a 12 copropietarios.", "Hace 2 días"));
    }

    public static List<Copropietario> getCopropietarios() {
        return copropietarios;
    }

    public static List<PagoPendiente> getPagosPendientes() {
        return pagosPendientes;
    }

    public static List<Notificacion> getNotificaciones() {
        return notificaciones;
    }
}
