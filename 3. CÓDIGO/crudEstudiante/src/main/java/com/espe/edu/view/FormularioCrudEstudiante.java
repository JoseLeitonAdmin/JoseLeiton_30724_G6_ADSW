package com.espe.edu.view;

import com.espe.edu.iterator.IIterador;
import com.espe.edu.mediator.IMediadorEstudiante;
import com.espe.edu.mediator.MediadorEstudiante;
import com.espe.edu.model.Estudiante;
import com.espe.edu.model.Resultado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Vista principal del sistema CRUD Estudiantes.
 * ─────────────────────────────────────────────────────────────────
 * Solo conoce la interfaz IMediadorEstudiante; no accede directamente
 * al Repositorio ni a los Comandos → desacoplamiento total (PATRÓN MEDIATOR).
 *
 * Para recorrer y mostrar los estudiantes en la tabla usa IIterador
 * en lugar de manejar índices o listas → encapsulación (PATRÓN ITERATOR).
 */
public class FormularioCrudEstudiante extends JFrame {

    // ── Campos de entrada ────────────────────────────────────────
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtEdad;

    // ── Tabla de resultados ──────────────────────────────────────
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // ── Mediador: único punto de comunicación con la lógica ──────
    // PATRÓN MEDIATOR: la Vista depende de la interfaz, no de la implementación
    private final IMediadorEstudiante mediador = new MediadorEstudiante();

    // ────────────────────────────────────────────────────────────
    //  Constructor — construye la interfaz Swing
    // ────────────────────────────────────────────────────────────
    public FormularioCrudEstudiante() {
        setTitle("CRUD Estudiantes — Command · Iterator · Mediator");
        setSize(640, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ── Formulario de entrada ────────────────────────────────
        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        txtId     = new JTextField(16);
        txtNombre = new JTextField(16);
        txtEdad   = new JTextField(16);

        txtId.setToolTipText("Ingrese el ID único del estudiante");
        txtNombre.setToolTipText("Ingrese el nombre completo");
        txtEdad.setToolTipText("Ingrese la edad (número positivo)");

        // UX: seleccionar texto al enfocar el campo
        FocusAdapter seleccionar = new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                ((JTextField) e.getSource()).selectAll();
            }
        };
        txtId.addFocusListener(seleccionar);
        txtNombre.addFocusListener(seleccionar);
        txtEdad.addFocusListener(seleccionar);

        gbc.gridx = 0; gbc.gridy = 0; panelForm.add(new JLabel("ID:"),     gbc);
        gbc.gridx = 1;                panelForm.add(txtId,                  gbc);
        gbc.gridx = 0; gbc.gridy = 1; panelForm.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;                panelForm.add(txtNombre,              gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelForm.add(new JLabel("Edad:"),   gbc);
        gbc.gridx = 1;                panelForm.add(txtEdad,                gbc);

        contenedor.add(panelForm);
        contenedor.add(Box.createVerticalStrut(12));

        // ── Botones CRUD ─────────────────────────────────────────
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));

        JButton btnAgregar    = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar   = new JButton("Eliminar");
        JButton btnMostrar    = new JButton("Mostrar Todo");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnMostrar);
        contenedor.add(panelBotones);

        // ── Tabla ────────────────────────────────────────────────
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Edad"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(580, 200));
        contenedor.add(scroll);

        add(contenedor);

        // ── Eventos de botones ───────────────────────────────────
        btnAgregar.addActionListener(   e -> accionAgregar());
        btnActualizar.addActionListener(e -> accionActualizar());
        btnEliminar.addActionListener(  e -> accionEliminar());
        btnMostrar.addActionListener(   e -> accionMostrarTodo());

        // Clic en fila → carga datos en el formulario
        tabla.getSelectionModel().addListSelectionListener(e -> cargarDesdeTabla());

        // Doble clic también carga los datos
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) cargarDesdeTabla();
            }
        });

        // ENTER navega entre campos
        txtId.addActionListener(    e -> txtNombre.requestFocus());
        txtNombre.addActionListener(e -> txtEdad.requestFocus());
        txtEdad.addActionListener(  e -> accionAgregar());

        // Mostrar datos iniciales al arrancar
        accionMostrarTodo();
    }

    // ────────────────────────────────────────────────────────────
    //  Acciones de los botones
    // ────────────────────────────────────────────────────────────

    /**
     * CREAR — delega al Mediador.
     * PATRÓN MEDIATOR: la Vista no sabe cómo se crea; solo pide al Mediador.
     */
    private void accionAgregar() {
        try {
            String id     = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            int    edad   = Integer.parseInt(txtEdad.getText().trim());

            Resultado r = mediador.agregar(id, nombre, edad);
            mostrarMensaje(r.getMensaje());

            if (r.isExito()) { limpiarCampos(); accionMostrarTodo(); }

        } catch (NumberFormatException ex) {
            mostrarMensaje("La edad debe ser un número entero válido.");
        }
    }

    /**
     * ACTUALIZAR — delega al Mediador.
     */
    private void accionActualizar() {
        try {
            String id     = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            int    edad   = Integer.parseInt(txtEdad.getText().trim());

            Resultado r = mediador.actualizar(id, nombre, edad);
            mostrarMensaje(r.getMensaje());

            if (r.isExito()) { limpiarCampos(); accionMostrarTodo(); }

        } catch (NumberFormatException ex) {
            mostrarMensaje("La edad debe ser un número entero válido.");
        }
    }

    /**
     * ELIMINAR — pide confirmación y delega al Mediador.
     */
    private void accionEliminar() {
        String id = txtId.getText().trim();

        int confirmar = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar el estudiante con ID: " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmar == JOptionPane.YES_OPTION) {
            Resultado r = mediador.eliminar(id);
            mostrarMensaje(r.getMensaje());
            if (r.isExito()) { limpiarCampos(); accionMostrarTodo(); }
        }
    }

    /**
     * LISTAR — usa el IIterador del Mediador para poblar la tabla.
     * PATRÓN ITERATOR: la Vista no conoce ArrayList ni índices internos.
     */
    private void accionMostrarTodo() {
        modeloTabla.setRowCount(0); // limpiar tabla

        // El Mediador devuelve un IIterador; la Vista lo recorre sin saber cómo está implementado
        IIterador<Estudiante> iterador = mediador.obtenerIterador();
        while (iterador.tieneSiguiente()) {
            Estudiante e = iterador.siguiente();
            modeloTabla.addRow(new Object[]{ e.getId(), e.getNombre(), e.getEdad() });
        }
    }

    // ────────────────────────────────────────────────────────────
    //  Utilidades de la Vista
    // ────────────────────────────────────────────────────────────

    /** Carga los datos de la fila seleccionada en el formulario. */
    private void cargarDesdeTabla() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtEdad.setText(modeloTabla.getValueAt(fila, 2).toString());
        }
    }

    /** Limpia todos los campos del formulario. */
    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtEdad.setText("");
        txtId.requestFocus();
    }

    /** Muestra un diálogo informativo con el mensaje dado. */
    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    // ────────────────────────────────────────────────────────────
    //  Punto de entrada
    // ────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormularioCrudEstudiante().setVisible(true));
    }
}
