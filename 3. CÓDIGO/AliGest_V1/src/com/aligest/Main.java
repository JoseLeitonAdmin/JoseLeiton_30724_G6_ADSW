package com.aligest;

import com.aligest.ui.MainFrame;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;

/**
 * Clase principal de inicio para la aplicación AliGest.
 */
public class Main {
    public static void main(String[] args) {
        // Configurar la interfaz gráfica para usar el diseño del sistema operativo nativo (ej. Windows)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback al diseño estándar de Swing en caso de error
        }

        // Ejecutar en el Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
