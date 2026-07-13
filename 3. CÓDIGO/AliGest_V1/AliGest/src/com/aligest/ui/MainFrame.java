package com.aligest.ui;

import com.aligest.adapter.CopropietarioCSVAdapter;
import com.aligest.adapter.ExportTarget;
import com.aligest.command.AprobarPagoCommand;
import com.aligest.command.Command;
import com.aligest.command.HistorialComandos;
import com.aligest.model.Copropietario;
import com.aligest.model.Notificacion;
import com.aligest.model.PagoPendiente;
import com.aligest.repository.DataMock;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Ventana principal de AliGest implementada en Swing.
 */
public class MainFrame extends JFrame {
    // Paleta de colores
    private static final Color PRIMARY = new Color(26, 54, 93);      // #1A365D - Navy
    private static final Color SECONDARY = new Color(36, 59, 83);    // #243B53 - Slate Blue
    private static final Color ACCENT = new Color(237, 137, 54);     // #ED8936 - Orange
    private static final Color BACKGROUND = new Color(247, 250, 252); // #F7FAFC - Light Gray
    private static final Color SUCCESS = new Color(56, 161, 105);    // #38A169 - Green
    private static final Color DANGER = new Color(229, 62, 62);      // #E53E3E - Red
    private static final Color WARNING = new Color(214, 158, 46);    // #D69E2E - Gold
    private static final Color LIGHT_BORDER = new Color(226, 232, 240); // #E2E8F0

    // Componentes del layout principal
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel viewsPanel = new JPanel(cardLayout);
    private JPanel loginView;
    private JPanel appView;

    // Modelos de tablas y componentes de datos
    private DefaultTableModel copropietariosModel;
    private DefaultTableModel pagosModel;
    private JPanel timelinePanel;

    // Etiquetas de métricas (para actualizar en tiempo real)
    private JLabel lblRecaudacion;
    private JLabel lblMorosidad;
    private JLabel lblPagosPendientesBadge;
    private JLabel lblPagosPendientesCard;
    private JLabel lblTotalPropietarios;

    // Control del Patrón Command
    private final HistorialComandos historial = new HistorialComandos();

    // Toasts activos
    private final List<JPanel> activeToasts = new ArrayList<>();

    public MainFrame() {
        setTitle("AliGest - Condominio La Primavera");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setMinimumSize(new Dimension(950, 580));
        setLocationRelativeTo(null);

        // Content Pane base que usa JLayeredPane para soportar los toasts flotantes
        JLayeredPane layeredPane = getLayeredPane();
        
        // Vista de Login e Interfaz Principal
        setupLoginView();
        setupAppView();

        // Panel contenedor para la base
        JPanel basePanel = new JPanel(new CardLayout());
        basePanel.add(loginView, "login");
        basePanel.add(appView, "app");
        basePanel.setBounds(0, 0, getWidth(), getHeight());
        
        layeredPane.add(basePanel, JLayeredPane.DEFAULT_LAYER);

        // Reposicionar el basePanel y los toasts al cambiar el tamaño de la ventana
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                basePanel.setBounds(0, 0, getWidth(), getHeight());
                repositionToasts();
            }
        });

        // Mostrar Login al inicio
        CardLayout baseLayout = (CardLayout) basePanel.getLayout();
        baseLayout.show(basePanel, "login");
    }

    // =========================================================================
    // VISTA DE LOGIN
    // =========================================================================
    private void setupLoginView() {
        loginView = new JPanel(new GridBagLayout());
        loginView.setBackground(PRIMARY);

        // Tarjeta central de Login (Glassmorphism-like)
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(LIGHT_BORDER, 1, true),
                new EmptyBorder(40, 40, 40, 40)
        ));
        card.setPreferredSize(new Dimension(400, 460));
        card.setMaximumSize(new Dimension(400, 460));

        // Título Logo
        JLabel lblLogo = new JLabel("AliGest");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblLogo.setForeground(PRIMARY);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Condominio La Primavera");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campos de login
        JLabel lblEmail = new JLabel("Correo Electrónico");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEmail.setForeground(PRIMARY);
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtEmail = new JTextField("admin@laprimavera.com");
        txtEmail.setMaximumSize(new Dimension(320, 35));
        txtEmail.setPreferredSize(new Dimension(320, 35));
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(PRIMARY);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPass = new JPasswordField("********");
        txtPass.setMaximumSize(new Dimension(320, 35));
        txtPass.setPreferredSize(new Dimension(320, 35));
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botón Ingresar
        JButton btnIngresar = createFlatButton("Ingresar al Sistema", PRIMARY, Color.WHITE, true);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setMaximumSize(new Dimension(320, 45));
        btnIngresar.setPreferredSize(new Dimension(320, 45));
        btnIngresar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hover effect en botón
        btnIngresar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnIngresar.setBackground(SECONDARY); }
            @Override
            public void mouseExited(MouseEvent e) { btnIngresar.setBackground(PRIMARY); }
        });

        btnIngresar.addActionListener(e -> {
            // Simular carga corta
            btnIngresar.setText("Cargando...");
            btnIngresar.setEnabled(false);
            
            Timer timer = new Timer(800, evt -> {
                btnIngresar.setText("Ingresar al Sistema");
                btnIngresar.setEnabled(true);
                
                // Cambiar a la vista de la aplicación
                JPanel base = (JPanel) getLayeredPane().getComponent(0);
                CardLayout cl = (CardLayout) base.getLayout();
                cl.show(base, "app");

                // Actualizar tablas e indicadores
                actualizarMetricas();
                llenarTablaCopropietarios();
                llenarTablaPagos();
                llenarNotificaciones();
                
                showToast("Sesión iniciada con éxito.", "success");
            });
            timer.setRepeats(false);
            timer.start();
        });

        // Ensamblar Tarjeta
        card.add(lblLogo);
        card.add(Box.createVerticalStrut(5));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(30));
        card.add(lblEmail);
        card.add(Box.createVerticalStrut(5));
        card.add(txtEmail);
        card.add(Box.createVerticalStrut(15));
        card.add(lblPass);
        card.add(Box.createVerticalStrut(5));
        card.add(txtPass);
        card.add(Box.createVerticalStrut(30));
        card.add(btnIngresar);

        loginView.add(card);
    }

    // =========================================================================
    // VISTA DE LA APLICACIÓN PRINCIPAL
    // =========================================================================
    private void setupAppView() {
        appView = new JPanel(new BorderLayout());
        appView.setBackground(BACKGROUND);

        // 1. Sidebar (Menú Izquierdo)
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(PRIMARY);
        sidebar.setPreferredSize(new Dimension(240, 750));
        sidebar.setBorder(new EmptyBorder(20, 15, 60, 15));

        // Logo en Sidebar
        JLabel lblSideLogo = new JLabel("AliGest");
        lblSideLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSideLogo.setForeground(Color.WHITE);
        lblSideLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblSideSub = new JLabel("Administración");
        lblSideSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSideSub.setForeground(ACCENT);
        lblSideSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Perfil de Usuario
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(SECONDARY);
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, SECONDARY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblAdminName = new JLabel("José Leiton");
        lblAdminName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAdminName.setForeground(Color.WHITE);
        
        JLabel lblAdminRole = new JLabel("Administrador");
        lblAdminRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAdminRole.setForeground(Color.LIGHT_GRAY);

        profilePanel.add(lblAdminName);
        profilePanel.add(Box.createVerticalStrut(3));
        profilePanel.add(lblAdminRole);

        // Botones de Navegación del Sidebar
        sidebar.add(lblSideLogo);
        sidebar.add(lblSideSub);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(profilePanel);
        sidebar.add(Box.createVerticalStrut(25));

        // Panel para agrupar botones de navegación
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(5, 1, 0, 8));
        navPanel.setBackground(PRIMARY);
        navPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.setMaximumSize(new Dimension(210, 240));

        JButton btnDash = createNavButton("Dashboard", "dashboard");
        JButton btnCoprop = createNavButton("Copropietarios", "copropietarios");
        
        // Botón especial de pagos con un Badge integrado
        JPanel btnPagosWrapper = new JPanel(new BorderLayout());
        btnPagosWrapper.setBackground(PRIMARY);
        btnPagosWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnPagos = createNavButton("Gestión de Pagos", "pagos");
        lblPagosPendientesBadge = new JLabel("3", SwingConstants.CENTER);
        lblPagosPendientesBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPagosPendientesBadge.setForeground(Color.WHITE);
        lblPagosPendientesBadge.setBackground(DANGER);
        lblPagosPendientesBadge.setOpaque(true);
        lblPagosPendientesBadge.setPreferredSize(new Dimension(20, 20));
        lblPagosPendientesBadge.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        // Panel contenedor para alinear el badge
        JPanel badgeAlign = new JPanel(new GridBagLayout());
        badgeAlign.setBackground(PRIMARY);
        badgeAlign.setBorder(new EmptyBorder(0, 0, 0, 5));
        badgeAlign.add(lblPagosPendientesBadge);
        
        btnPagosWrapper.add(btnPagos, BorderLayout.CENTER);
        btnPagosWrapper.add(badgeAlign, BorderLayout.EAST);

        JButton btnNotif = createNavButton("Notificaciones", "notificaciones");
        JButton btnRep = createNavButton("Reportes", "reportes");

        navPanel.add(btnDash);
        navPanel.add(btnCoprop);
        navPanel.add(btnPagosWrapper);
        navPanel.add(btnNotif);
        navPanel.add(btnRep);
        
        sidebar.add(navPanel);
        sidebar.add(Box.createVerticalGlue());

        // Botón Cerrar Sesión
        JButton btnCerrar = new JButton("Cerrar Sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(SECONDARY);
                } else {
                    g2.setColor(PRIMARY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCerrar.setForeground(new Color(203, 213, 225));
        btnCerrar.setBackground(PRIMARY);
        btnCerrar.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setOpaque(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setHorizontalAlignment(SwingConstants.CENTER);
        btnCerrar.setMaximumSize(new Dimension(180, 38));
        btnCerrar.setPreferredSize(new Dimension(180, 38));

        btnCerrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCerrar.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnCerrar.setForeground(new Color(203, 213, 225));
            }
        });
        
        btnCerrar.addActionListener(e -> {
            // Re-inicializar datos y volver al login
            DataMock.inicializarDatos();
            historial.limpiar();
            JPanel base = (JPanel) getLayeredPane().getComponent(0);
            CardLayout cl = (CardLayout) base.getLayout();
            cl.show(base, "login");
        });

        // Wrapper para centrar el botón en el sidebar
        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoutWrapper.setBackground(PRIMARY);
        logoutWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutWrapper.setMaximumSize(new Dimension(210, 45));
        logoutWrapper.add(btnCerrar);
        
        sidebar.add(logoutWrapper);

        // 2. Encabezado Superior (Header)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_BORDER));
        header.setPreferredSize(new Dimension(800, 60));
        header.setBorder(new EmptyBorder(0, 25, 0, 25));

        JLabel lblHeaderTitle = new JLabel("Dashboard Principal");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeaderTitle.setForeground(PRIMARY);

        // Fecha Actual Formateada
        String dateStr = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES")).format(new Date());
        JLabel lblHeaderDate = new JLabel(dateStr);
        lblHeaderDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHeaderDate.setForeground(Color.GRAY);

        header.add(lblHeaderTitle, BorderLayout.WEST);
        header.add(lblHeaderDate, BorderLayout.EAST);

        // 3. Paneles de Vistas (Vistas Dinámicas usando CardLayout)
        setupDashboardPanel();
        setupCopropietariosPanel();
        setupPagosPanel();
        setupNotificacionesPanel();
        setupReportesPanel();

        // Ensamblar todo el Main Panel
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.add(header, BorderLayout.NORTH);
        
        // ScrollPane para el área central de trabajo
        JScrollPane scrollArea = new JScrollPane(viewsPanel);
        scrollArea.setBorder(null);
        scrollArea.getVerticalScrollBar().setUnitIncrement(12);
        
        mainContent.add(scrollArea, BorderLayout.CENTER);

        appView.add(sidebar, BorderLayout.WEST);
        appView.add(mainContent, BorderLayout.CENTER);

        // Listener para cambiar el título del Header según la vista activa
        viewsPanel.putClientProperty("header", lblHeaderTitle);
    }

    private JButton createFlatButton(String text, Color bg, Color fg, boolean rounded) {
        JButton btn = new JButton(text) {
            @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                if (enabled) {
                    setForeground(fg);
                } else {
                    setForeground(new Color(148, 163, 184));
                }
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) {
                    g2.setColor(new Color(226, 232, 240));
                } else {
                    if (getModel().isRollover()) {
                        if (getBackground().equals(PRIMARY)) {
                            g2.setColor(SECONDARY);
                        } else if (getBackground().equals(ACCENT)) {
                            g2.setColor(new Color(221, 107, 32));
                        } else if (getBackground().equals(SUCCESS)) {
                            g2.setColor(new Color(34, 197, 94));
                        } else {
                            g2.setColor(getBackground().darker());
                        }
                    } else {
                        g2.setColor(getBackground());
                    }
                }
                if (rounded) {
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else {
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createNavButton(String text, String targetView) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Boolean active = (Boolean) getClientProperty("active");
                if (active != null && active) {
                    g2.setColor(SECONDARY);
                } else if (getModel().isRollover()) {
                    g2.setColor(SECONDARY);
                } else {
                    g2.setColor(PRIMARY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(PRIMARY);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("active", false);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Boolean active = (Boolean) btn.getClientProperty("active");
                if (active == null || !active) {
                    btn.setForeground(Color.WHITE);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                Boolean active = (Boolean) btn.getClientProperty("active");
                if (active == null || !active) {
                    btn.setForeground(new Color(203, 213, 225));
                }
            }
        });

        btn.addActionListener(e -> navigate(targetView, btn));
        return btn;
    }

    private void navigate(String targetView, JButton sourceButton) {
        // Resetear estilos de todos los botones de navegación
        Container parent = sourceButton.getParent();
        if (parent != null) {
            resetButtonsInContainer(parent);
        }
        
        // Si el botón está dentro de un Wrapper, resetear el wrapper también
        Container grandParent = sourceButton.getParent().getParent();
        if (grandParent != null && grandParent.getClass().equals(JPanel.class)) {
            resetButtonsInContainer(grandParent);
        }

        sourceButton.putClientProperty("active", true);
        sourceButton.setForeground(Color.WHITE);

        cardLayout.show(viewsPanel, targetView);
        
        // Actualizar título del Header
        JLabel headerTitle = (JLabel) viewsPanel.getClientProperty("header");
        if (headerTitle != null) {
            switch (targetView) {
                case "dashboard": headerTitle.setText("Dashboard Principal"); break;
                case "copropietarios": headerTitle.setText("Directorio de Copropietarios"); break;
                case "pagos": headerTitle.setText("Validación de Pagos"); break;
                case "notificaciones": headerTitle.setText("Log de Notificaciones (WhatsApp API)"); break;
                case "reportes": headerTitle.setText("Reportes y Analíticas Financieras"); break;
            }
        }
    }

    private void resetButtonsInContainer(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                b.putClientProperty("active", false);
                b.setForeground(new Color(203, 213, 225));
            } else if (c instanceof JPanel) {
                resetButtonsInContainer((JPanel) c);
            }
        }
    }

    // =========================================================================
    // PANEL: DASHBOARD
    // =========================================================================
    private void setupDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. Fila de Tarjetas de Indicadores (Métricas)
        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsGrid.setBackground(BACKGROUND);
        cardsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        lblRecaudacion = new JLabel("$1,056.00");
        cardsGrid.add(createMetricCard("Recaudación del Mes", lblRecaudacion, "80% de la meta", SUCCESS));

        lblMorosidad = new JLabel("$264.00");
        cardsGrid.add(createMetricCard("Morosidad Total", lblMorosidad, "12% de recargo", DANGER));

        lblPagosPendientesCard = new JLabel("3");
        cardsGrid.add(createMetricCard("Pagos por Validar", lblPagosPendientesCard, "Requieren acción", ACCENT));

        lblTotalPropietarios = new JLabel("60");
        cardsGrid.add(createMetricCard("Casas Activas", lblTotalPropietarios, "Primavera", PRIMARY));

        panel.add(cardsGrid);
        panel.add(Box.createVerticalStrut(25));

        // 2. Fila de Gráficos Simulados
        JPanel chartsGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsGrid.setBackground(BACKGROUND);
        chartsGrid.setPreferredSize(new Dimension(800, 320));
        chartsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        // Gráfico de Barras: Recaudación vs Mora
        JPanel barChartPanel = new JPanel(new BorderLayout());
        barChartPanel.setBackground(Color.WHITE);
        barChartPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(LIGHT_BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        JLabel lblChart1Title = new JLabel("Flujo de Recaudación (Últimos 6 meses)");
        lblChart1Title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblChart1Title.setForeground(PRIMARY);
        barChartPanel.add(lblChart1Title, BorderLayout.NORTH);
        
        // Dibujo del gráfico
        barChartPanel.add(new CustomBarChart(), BorderLayout.CENTER);

        // Gráfico Circular: Estado de Cuentas
        JPanel pieChartPanel = new JPanel(new BorderLayout());
        pieChartPanel.setBackground(Color.WHITE);
        pieChartPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(LIGHT_BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        JLabel lblChart2Title = new JLabel("Distribución de Casas");
        lblChart2Title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblChart2Title.setForeground(PRIMARY);
        pieChartPanel.add(lblChart2Title, BorderLayout.NORTH);
        
        // Dibujo del gráfico
        pieChartPanel.add(new CustomPieChart(), BorderLayout.CENTER);

        chartsGrid.add(barChartPanel);
        chartsGrid.add(pieChartPanel);

        panel.add(chartsGrid);
        viewsPanel.add(panel, "dashboard");
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, String footer, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        new LineBorder(LIGHT_BORDER, 1, true),
                        new EmptyBorder(15, 15, 15, 15)
                )
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(Color.GRAY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(PRIMARY);

        JLabel lblFooter = new JLabel(footer);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFooter.setForeground(accentColor);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(lblFooter);

        return card;
    }

    // Dibujadores personalizados para simular Chart.js
    private static class CustomBarChart extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padding = 30;

            // Dibujar Eje X y Y
            g2.setColor(LIGHT_BORDER);
            g2.drawLine(padding, h - padding, w - padding, h - padding);

            // Datos de prueba (Nov, Dic, Ene, Feb, Mar, Abr)
            String[] meses = {"Nov", "Dic", "Ene", "Feb", "Mar", "Abr"};
            double[] recaudacion = {1100, 1150, 980, 1200, 1050, 1056};
            double[] mora = {150, 120, 250, 100, 220, 264};

            int n = meses.length;
            int barWidth = (w - padding * 2) / (n * 3);
            int step = (w - padding * 2) / n;

            double maxVal = 1500.0; // Escala máxima

            for (int i = 0; i < n; i++) {
                int x = padding + i * step + barWidth;

                // Barra de Recaudación (PRIMARY)
                int barH1 = (int) ((recaudacion[i] / maxVal) * (h - padding * 2));
                g2.setColor(PRIMARY);
                g2.fillRect(x, h - padding - barH1, barWidth, barH1);

                // Barra de Mora (DANGER)
                int barH2 = (int) ((mora[i] / maxVal) * (h - padding * 2));
                g2.setColor(DANGER);
                g2.fillRect(x + barWidth + 2, h - padding - barH2, barWidth, barH2);

                // Nombre del Mes
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.drawString(meses[i], x + barWidth / 2, h - padding + 15);
            }
        }
    }

    private static class CustomPieChart extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h) - 60;
            int x = (w - size) / 2;
            int y = (h - size) / 2;

            // Calcular porcentajes: 48 al día (80%), 12 en mora (20%)
            int angleDia = (int) (0.80 * 360);
            int angleMora = 360 - angleDia;

            // Dibujar sectores
            g2.setColor(SUCCESS);
            g2.fillArc(x, y, size, size, 90, angleDia);

            g2.setColor(DANGER);
            g2.fillArc(x, y, size, size, 90 + angleDia, angleMora);

            // Efecto Doughnut (círculo blanco en el centro)
            int innerSize = (int) (size * 0.6);
            int ix = x + (size - innerSize) / 2;
            int iy = y + (size - innerSize) / 2;
            g2.setColor(Color.WHITE);
            g2.fillOval(ix, iy, innerSize, innerSize);

            // Texto interno
            g2.setColor(PRIMARY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2.drawString("60", w / 2 - 10, h / 2);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString("Casas", w / 2 - 13, h / 2 + 13);
        }
    }

    // =========================================================================
    // PANEL: COPROPIETARIOS
    // =========================================================================
    private void setupCopropietariosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Cabecera de la sección
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BACKGROUND);
        topBar.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Directorio de Copropietarios");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(PRIMARY);

        JButton btnNuevo = createFlatButton("+ Nuevo Copropietario", PRIMARY, Color.WHITE, true);
        btnNuevo.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnNuevo.addActionListener(e -> openNuevoCopropietarioDialog());
        btnNuevo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnNuevo.setBackground(SECONDARY); }
            @Override
            public void mouseExited(MouseEvent e) { btnNuevo.setBackground(PRIMARY); }
        });

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(btnNuevo, BorderLayout.EAST);

        // Tabla de Datos
        String[] columnas = {"Casa", "Copropietario", "Alícuota (%)", "Estado Actual", "Contacto"};
        copropietariosModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(copropietariosModel);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(LIGHT_BORDER);
        table.setShowVerticalLines(false);

        // Estilos de la Cabecera de la Tabla
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_BORDER));

        // Renderizado personalizado de la columna Estado (badge verde/rojo)
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                String estado = (String) value;
                if ("Al Día".equals(estado)) {
                    label.setForeground(SUCCESS);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    label.setForeground(DANGER);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                return label;
            }
        });

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(new LineBorder(LIGHT_BORDER, 1, true));

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scrollTable, BorderLayout.CENTER);

        viewsPanel.add(panel, "copropietarios");
    }

    private void llenarTablaCopropietarios() {
        copropietariosModel.setRowCount(0);
        // Mostrar los primeros 10 copropietarios en la tabla principal como en el JS
        List<Copropietario> lista = DataMock.getCopropietarios();
        int limit = Math.min(10, lista.size());
        for (int i = 0; i < limit; i++) {
            Copropietario c = lista.get(i);
            copropietariosModel.addRow(new Object[]{
                c.getCasa(),
                c.getNombre(),
                String.format(Locale.US, "%.2f%%", c.getAlicuota()),
                c.getEstado(),
                c.getTelefono()
            });
        }
    }

    private void openNuevoCopropietarioDialog() {
        JDialog dlg = new JDialog(this, "Nuevo Copropietario", true);
        dlg.setLayout(new BorderLayout());
        dlg.setSize(450, 480);
        dlg.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Campos
        JTextField txtCasa = createDialogField(mainPanel, "Casa / Departamento (Ej: Casa 15)");
        JTextField txtAlicuota = createDialogField(mainPanel, "Alícuota (%)");
        JTextField txtNombre = createDialogField(mainPanel, "Nombre Completo");
        JTextField txtTelefono = createDialogField(mainPanel, "Teléfono");
        JTextField txtCorreo = createDialogField(mainPanel, "Correo Electrónico");

        // Campo de Mora
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkPanel.setBackground(Color.WHITE);
        JCheckBox chkMora = new JCheckBox("Registrar con deuda pendiente (En Mora)");
        chkMora.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkMora.setBackground(Color.WHITE);
        checkPanel.add(chkMora);
        mainPanel.add(checkPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Botones de acción
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(new Color(248, 250, 252));
        actions.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, LIGHT_BORDER));

        JButton btnCancelar = createFlatButton("Cancelar", new Color(226, 232, 240), PRIMARY, true);
        btnCancelar.setBorder(new EmptyBorder(6, 12, 6, 12));
        btnCancelar.addActionListener(e -> dlg.dispose());
        btnCancelar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnCancelar.setBackground(new Color(203, 213, 225)); }
            @Override
            public void mouseExited(MouseEvent e) { btnCancelar.setBackground(new Color(226, 232, 240)); }
        });

        JButton btnGuardar = createFlatButton("Guardar Registro", PRIMARY, Color.WHITE, true);
        btnGuardar.setBorder(new EmptyBorder(6, 12, 6, 12));
        
        btnGuardar.addActionListener(e -> {
            try {
                String casa = txtCasa.getText().trim();
                String nombre = txtNombre.getText().trim();
                double alicuota = Double.parseDouble(txtAlicuota.getText().trim());
                String telefono = txtTelefono.getText().trim();
                String correo = txtCorreo.getText().trim();

                if (casa.isEmpty() || nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Casa y Nombre son requeridos.", "Error de validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String estado = chkMora.isSelected() ? "En Mora" : "Al Día";

                Copropietario nuevo = new Copropietario(
                    System.currentTimeMillis(),
                    casa, nombre, alicuota, estado, telefono, correo
                );

                // Añadir al inicio
                DataMock.getCopropietarios().add(0, nuevo);
                
                llenarTablaCopropietarios();
                actualizarMetricas();
                dlg.dispose();
                
                showToast("Propietario registrado exitosamente en " + casa + ".", "success");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "La alícuota debe ser un número decimal válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        actions.add(btnCancelar);
        actions.add(btnGuardar);

        dlg.add(mainPanel, BorderLayout.CENTER);
        dlg.add(actions, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private JTextField createDialogField(JPanel container, String labelText) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        tf.setPreferredSize(new Dimension(380, 32));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(lbl);
        container.add(Box.createVerticalStrut(4));
        container.add(tf);
        container.add(Box.createVerticalStrut(12));
        
        return tf;
    }

    // =========================================================================
    // PANEL: GESTIÓN DE PAGOS
    // =========================================================================
    private void setupPagosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Cabecera de la sección
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BACKGROUND);
        topBar.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Validación de Pagos Pendientes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(PRIMARY);

        JButton btnSimular = createFlatButton("Simular Nuevo Pago", ACCENT, Color.WHITE, true);
        btnSimular.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnSimular.addActionListener(e -> openSimularPagoDialog());
        btnSimular.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnSimular.setBackground(new Color(221, 107, 32)); }
            @Override
            public void mouseExited(MouseEvent e) { btnSimular.setBackground(ACCENT); }
        });

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(btnSimular, BorderLayout.EAST);

        // Tabla de Pagos Pendientes
        String[] columnas = {"ID", "Fecha", "Casa / Propietario", "Monto", "Mes Expensa", "Estado Mora"};
        pagosModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(pagosModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(LIGHT_BORDER);
        table.setShowVerticalLines(false);

        // Estilos de la Cabecera de la Tabla
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_BORDER));

        // Custom renderer para mostrar los estados mora con badge
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                String estado = (String) value;
                if ("Mora 12%".equals(estado)) {
                    label.setForeground(DANGER);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    label.setForeground(SUCCESS);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                return label;
            }
        });

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(new LineBorder(LIGHT_BORDER, 1, true));

        // Doble clic o selección + click para Validar Pago
        JPanel bottomAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomAction.setBackground(BACKGROUND);
        bottomAction.setBorder(new EmptyBorder(0, 0, 50, 0));
        JButton btnRevisar = createFlatButton("Validar Comprobante Seleccionado", PRIMARY, Color.WHITE, true);
        btnRevisar.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnRevisar.setEnabled(false);
        btnRevisar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { if (btnRevisar.isEnabled()) btnRevisar.setBackground(SECONDARY); }
            @Override
            public void mouseExited(MouseEvent e) { if (btnRevisar.isEnabled()) btnRevisar.setBackground(PRIMARY); }
        });
        
        btnRevisar.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                long id = (Long) pagosModel.getValueAt(selectedRow, 0);
                openValidarPagoDialog(id);
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            btnRevisar.setEnabled(table.getSelectedRow() != -1);
        });

        // Doble clic abre el validador directo
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    long id = (Long) pagosModel.getValueAt(table.getSelectedRow(), 0);
                    openValidarPagoDialog(id);
                }
            }
        });

        bottomAction.add(btnRevisar);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scrollTable, BorderLayout.CENTER);
        panel.add(bottomAction, BorderLayout.SOUTH);

        viewsPanel.add(panel, "pagos");
    }

    private void llenarTablaPagos() {
        pagosModel.setRowCount(0);
        List<PagoPendiente> lista = DataMock.getPagosPendientes();
        for (PagoPendiente p : lista) {
            pagosModel.addRow(new Object[]{
                p.getId(),
                p.getFecha(),
                p.getCasa() + " - " + p.getNombre(),
                String.format(Locale.US, "$%.2f", p.getMontoFinal()),
                p.getExpensa(),
                p.isMora() ? "Mora 12%" : "En Plazo"
            });
        }
    }

    private void openSimularPagoDialog() {
        JDialog dlg = new JDialog(this, "Simulador de Comprobantes", true);
        dlg.setLayout(new BorderLayout());
        dlg.setSize(400, 360);
        dlg.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Combo de Copropietarios (Primeros 15 para simulación rápida)
        JLabel lblCop = new JLabel("Copropietario Emisor");
        lblCop.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCop.setForeground(PRIMARY);
        lblCop.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> cbOwners = new JComboBox<>();
        List<Copropietario> owners = DataMock.getCopropietarios();
        int limit = Math.min(15, owners.size());
        for (int i = 0; i < limit; i++) {
            Copropietario c = owners.get(i);
            cbOwners.addItem(c.getCasa() + " - " + c.getNombre());
        }
        cbOwners.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbOwners.setPreferredSize(new Dimension(360, 35));
        cbOwners.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(lblCop);
        mainPanel.add(Box.createVerticalStrut(4));
        mainPanel.add(cbOwners);
        mainPanel.add(Box.createVerticalStrut(15));

        // Campo Monto y Mes
        JTextField txtMonto = createDialogField(mainPanel, "Monto Depositado ($)");
        txtMonto.setText("22.00");
        
        JTextField txtMes = createDialogField(mainPanel, "Mes de Expensa que paga");
        txtMes.setText("Mayo 2026");

        // Checkbox Mora
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkPanel.setBackground(Color.WHITE);
        JCheckBox chkMora = new JCheckBox("Aplicar estado de mora (+12% recargo)");
        chkMora.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkMora.setBackground(Color.WHITE);
        checkPanel.add(chkMora);
        
        mainPanel.add(checkPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Botones de acción
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(new Color(248, 250, 252));
        actions.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, LIGHT_BORDER));

        JButton btnCancelar = createFlatButton("Cancelar", new Color(226, 232, 240), PRIMARY, true);
        btnCancelar.setBorder(new EmptyBorder(6, 12, 6, 12));
        btnCancelar.addActionListener(e -> dlg.dispose());
        btnCancelar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnCancelar.setBackground(new Color(203, 213, 225)); }
            @Override
            public void mouseExited(MouseEvent e) { btnCancelar.setBackground(new Color(226, 232, 240)); }
        });

        JButton btnAdicionar = createFlatButton("Añadir a Bandeja", ACCENT, Color.WHITE, true);
        btnAdicionar.setBorder(new EmptyBorder(6, 12, 6, 12));
        
        btnAdicionar.addActionListener(e -> {
            try {
                String comboVal = (String) cbOwners.getSelectedItem();
                if (comboVal == null) return;
                
                String[] parts = comboVal.split(" - ");
                String casa = parts[0];
                String nombre = parts[1];
                
                double monto = Double.parseDouble(txtMonto.getText().trim());
                String mes = txtMes.getText().trim();
                boolean mora = chkMora.isSelected();

                String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

                PagoPendiente nuevoPago = new PagoPendiente(
                    System.currentTimeMillis(),
                    today, casa, nombre, monto, mora, mes
                );

                DataMock.getPagosPendientes().add(0, nuevoPago);
                
                llenarTablaPagos();
                actualizarMetricas();
                dlg.dispose();
                
                showToast("El sistema recibió un nuevo comprobante de " + casa + ".", "info");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "El monto debe ser un valor numérico.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        actions.add(btnCancelar);
        actions.add(btnAdicionar);

        dlg.add(mainPanel, BorderLayout.CENTER);
        dlg.add(actions, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void openValidarPagoDialog(long idPago) {
        // Encontrar el pago pendiente
        PagoPendiente pago = null;
        for (PagoPendiente p : DataMock.getPagosPendientes()) {
            if (p.getId() == idPago) {
                pago = p;
                break;
            }
        }
        if (pago == null) return;

        final PagoPendiente selectedPago = pago;

        JDialog dlg = new JDialog(this, "Validar Comprobante", true);
        dlg.setLayout(new BorderLayout());
        dlg.setSize(620, 380);
        dlg.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Panel Izquierdo: Detalles de Pago
        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setBackground(Color.WHITE);

        addDetailLabel(details, "COPROPIETARIO", selectedPago.getNombre());
        addDetailLabel(details, "CASA", selectedPago.getCasa());
        
        JPanel doubleCol = new JPanel(new GridLayout(1, 2, 10, 0));
        doubleCol.setBackground(Color.WHITE);
        addDetailLabel(doubleCol, "MES A PAGAR", selectedPago.getExpensa());
        addDetailLabel(doubleCol, "ALÍCUOTA BASE", String.format(Locale.US, "$%.2f", selectedPago.getMonto()));
        details.add(doubleCol);
        details.add(Box.createVerticalStrut(15));

        // Caja de recargo de Mora si aplica
        if (selectedPago.isMora()) {
            JPanel alertBox = new JPanel();
            alertBox.setLayout(new BoxLayout(alertBox, BoxLayout.Y_AXIS));
            alertBox.setBackground(new Color(254, 242, 242));
            alertBox.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(DANGER, 1, true),
                    new EmptyBorder(10, 15, 10, 15)
            ));
            
            JLabel lblAlertTitle = new JLabel("⚠️ Aplicación de Mora (12%)");
            lblAlertTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblAlertTitle.setForeground(DANGER);

            JLabel lblAlertMsg = new JLabel("Pago atrasado. Recargo: +$" + selectedPago.getRecargoMora());
            lblAlertMsg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblAlertMsg.setForeground(new Color(153, 27, 27));

            JLabel lblTotal = new JLabel("Total a Validar: $" + selectedPago.getMontoFinal());
            lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblTotal.setForeground(DANGER);

            alertBox.add(lblAlertTitle);
            alertBox.add(Box.createVerticalStrut(3));
            alertBox.add(lblAlertMsg);
            alertBox.add(Box.createVerticalStrut(5));
            alertBox.add(lblTotal);

            details.add(alertBox);
        } else {
            JPanel cleanBox = new JPanel();
            cleanBox.setLayout(new BoxLayout(cleanBox, BoxLayout.Y_AXIS));
            cleanBox.setBackground(new Color(240, 253, 244));
            cleanBox.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(SUCCESS, 1, true),
                    new EmptyBorder(10, 15, 10, 15)
            ));

            JLabel lblCleanTitle = new JLabel("✅ Pago en Plazo");
            lblCleanTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblCleanTitle.setForeground(SUCCESS);

            JLabel lblTotal = new JLabel("Total a Validar: $" + selectedPago.getMontoFinal());
            lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblTotal.setForeground(SUCCESS);

            cleanBox.add(lblCleanTitle);
            cleanBox.add(Box.createVerticalStrut(5));
            cleanBox.add(lblTotal);
            details.add(cleanBox);
        }

        // Panel Derecho: Vista del Comprobante (Simulada)
        JPanel receipt = new JPanel(new GridBagLayout());
        receipt.setBackground(new Color(248, 250, 252));
        receipt.setBorder(new LineBorder(LIGHT_BORDER, 2, true));
        
        JLabel lblDoc = new JLabel("📄 comprobante.jpg");
        lblDoc.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblDoc.setForeground(Color.GRAY);
        receipt.add(lblDoc);

        mainPanel.add(details);
        mainPanel.add(receipt);

        // Botones de acción
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(new Color(248, 250, 252));
        actions.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, LIGHT_BORDER));

        JButton btnCancelar = createFlatButton("Cancelar", new Color(226, 232, 240), PRIMARY, true);
        btnCancelar.setBorder(new EmptyBorder(6, 12, 6, 12));
        btnCancelar.addActionListener(e -> dlg.dispose());
        btnCancelar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnCancelar.setBackground(new Color(203, 213, 225)); }
            @Override
            public void mouseExited(MouseEvent e) { btnCancelar.setBackground(new Color(226, 232, 240)); }
        });

        JButton btnRechazar = createFlatButton("Rechazar", new Color(254, 242, 242), DANGER, true);
        btnRechazar.setBorder(new EmptyBorder(6, 12, 6, 12));
        btnRechazar.addActionListener(e -> {
            DataMock.getPagosPendientes().remove(selectedPago);
            llenarTablaPagos();
            actualizarMetricas();
            dlg.dispose();
            showToast("Pago rechazado.", "error");
        });
        btnRechazar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnRechazar.setBackground(new Color(254, 226, 226)); }
            @Override
            public void mouseExited(MouseEvent e) { btnRechazar.setBackground(new Color(254, 242, 242)); }
        });

        JButton btnAprobar = createFlatButton("Aprobar Pago", SUCCESS, Color.WHITE, true);
        btnAprobar.setBorder(new EmptyBorder(6, 12, 6, 12));
        
        btnAprobar.addActionListener(e -> {
            // Ejecutar la aprobación a través del Patrón Command
            Command cmd = new AprobarPagoCommand(selectedPago.getId(), DataMock.getPagosPendientes(), () -> {
                // Callback al ejecutar o deshacer
                llenarTablaPagos();
                actualizarMetricas();
            });

            historial.ejecutar(cmd);
            dlg.dispose();

            // Notificación flotante con Deshacer (Undo)
            showToastWithUndo(
                "Pago de " + selectedPago.getNombre() + " aprobado. Recibo generado.",
                "success",
                () -> {
                    historial.deshacer();
                    showToast("Acción revertida. El pago vuelve a estar pendiente.", "info");
                }
            );

            // Simular notificación secundaria de WhatsApp API tras 1.5 segundos
            Timer wppTimer = new Timer(1500, evt -> {
                // Registrar en logs de notificaciones
                DataMock.getNotificaciones().add(0, new Notificacion(
                    "success",
                    "WhatsApp API: Notificado a " + selectedPago.getNombre() + " sobre pago en " + selectedPago.getCasa() + ".",
                    "Hace 1 minuto"
                ));
                llenarNotificaciones();
                showToast("💬 WhatsApp API: Notificado a " + selectedPago.getNombre(), "info");
            });
            wppTimer.setRepeats(false);
            wppTimer.start();
        });

        actions.add(btnCancelar);
        actions.add(btnRechazar);
        actions.add(btnAprobar);

        dlg.add(mainPanel, BorderLayout.CENTER);
        dlg.add(actions, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void addDetailLabel(JPanel container, String title, String val) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(0, 0, 10, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblVal = new JLabel(val);
        lblVal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblVal.setForeground(PRIMARY);

        row.add(lblTitle);
        row.add(Box.createVerticalStrut(2));
        row.add(lblVal);

        container.add(row);
    }

    // =========================================================================
    // PANEL: NOTIFICACIONES LOG (WHATSAPP API)
    // =========================================================================
    private void setupNotificacionesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel("Historial de Comunicaciones WhatsApp API");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setBackground(BACKGROUND);

        JScrollPane scroll = new JScrollPane(timelinePanel);
        scroll.setBorder(null);

        panel.add(scroll, BorderLayout.CENTER);
        viewsPanel.add(panel, "notificaciones");
    }

    private void llenarNotificaciones() {
        timelinePanel.removeAll();
        List<Notificacion> list = DataMock.getNotificaciones();
        for (Notificacion n : list) {
            JPanel card = new JPanel(new BorderLayout(15, 0));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(LIGHT_BORDER, 1, true),
                    new EmptyBorder(12, 15, 12, 15)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

            // Icono simulado con color según tipo
            JLabel lblIcon = new JLabel();
            lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
            
            if ("success".equals(n.getType())) {
                lblIcon.setText("✅");
                lblIcon.setForeground(SUCCESS);
            } else if ("warning".equals(n.getType())) {
                lblIcon.setText("⚠️");
                lblIcon.setForeground(WARNING);
            } else {
                lblIcon.setText("ℹ️");
                lblIcon.setForeground(PRIMARY);
            }
            
            JPanel textGroup = new JPanel();
            textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));
            textGroup.setBackground(Color.WHITE);

            JLabel lblMsg = new JLabel("<html><body style='width: 500px'>" + n.getMsg() + "</body></html>");
            lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblMsg.setForeground(PRIMARY);

            JLabel lblTime = new JLabel(n.getTime());
            lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblTime.setForeground(Color.GRAY);

            textGroup.add(lblMsg);
            textGroup.add(Box.createVerticalStrut(3));
            textGroup.add(lblTime);

            card.add(lblIcon, BorderLayout.WEST);
            card.add(textGroup, BorderLayout.CENTER);

            timelinePanel.add(card);
            timelinePanel.add(Box.createVerticalStrut(10));
        }
        timelinePanel.revalidate();
        timelinePanel.repaint();
    }

    // =========================================================================
    // PANEL: REPORTES (MÓDULO DE ADAPTER)
    // =========================================================================
    private void setupReportesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Caja superior de Exportación
        JPanel topBox = new JPanel(new BorderLayout());
        topBox.setBackground(Color.WHITE);
        topBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(LIGHT_BORDER, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        topBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel textGroup = new JPanel();
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));
        textGroup.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Reportes y Analíticas Financieras");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(PRIMARY);

        JLabel lblDesc = new JLabel("Métricas actualizadas del condominio");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(Color.GRAY);

        textGroup.add(lblTitle);
        textGroup.add(Box.createVerticalStrut(4));
        textGroup.add(lblDesc);

        JButton btnExport = createFlatButton("Exportar Data (Excel/CSV)", SUCCESS, Color.WHITE, true);
        btnExport.setBorder(new EmptyBorder(10, 15, 10, 15));
        btnExport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnExport.setBackground(new Color(34, 197, 94)); }
            @Override
            public void mouseExited(MouseEvent e) { btnExport.setBackground(SUCCESS); }
        });
        
        btnExport.addActionListener(e -> exportarReporteCSV());

        topBox.add(textGroup, BorderLayout.WEST);
        topBox.add(btnExport, BorderLayout.EAST);
        panel.add(topBox);
        panel.add(Box.createVerticalStrut(20));

        // Tarjetas de analítica histórica
        JPanel metricRow = new JPanel(new GridLayout(1, 3, 15, 0));
        metricRow.setBackground(BACKGROUND);
        metricRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        metricRow.add(createReportMetricCard("Tasa de Morosidad Histórica", "18.5%", DANGER));
        metricRow.add(createReportMetricCard("Recaudación Promedio Mensual", "$1,120.00", PRIMARY));
        metricRow.add(createReportMetricCard("Proyección Anual (Estimada)", "$13,440.00", SUCCESS));

        panel.add(metricRow);
        panel.add(Box.createVerticalStrut(20));

        // Espacio para Gráfico de Tendencia
        JPanel graphContainer = new JPanel(new BorderLayout());
        graphContainer.setBackground(Color.WHITE);
        graphContainer.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(LIGHT_BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblGraphTitle = new JLabel("Tendencia de Pagos a Tiempo vs Atrasados");
        lblGraphTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGraphTitle.setForeground(PRIMARY);
        graphContainer.add(lblGraphTitle, BorderLayout.NORTH);
        
        graphContainer.add(new CustomLineChart(), BorderLayout.CENTER);

        panel.add(graphContainer);

        viewsPanel.add(panel, "reportes");
    }

    private JPanel createReportMetricCard(String title, String val, Color c) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(LIGHT_BORDER, 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblVal = new JLabel(val);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(c);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(lblVal);

        return card;
    }

    private static class CustomLineChart extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padding = 30;

            // Ejes
            g2.setColor(LIGHT_BORDER);
            g2.drawLine(padding, h - padding, w - padding, h - padding);

            String[] meses = {"Nov", "Dic", "Ene", "Feb", "Mar", "Abr"};
            int[] pagosTiempo = {45, 50, 42, 48, 47, 48};
            int[] pagosMora = {15, 10, 18, 12, 13, 12};

            int n = meses.length;
            int step = (w - padding * 2) / (n - 1);
            int maxVal = 60; // Total de casas

            // Dibujar línea de Pagos a Tiempo (SUCCESS)
            g2.setColor(SUCCESS);
            g2.setStroke(new BasicStroke(2.0f));
            int prevX = -1, prevY = -1;
            for (int i = 0; i < n; i++) {
                int x = padding + i * step;
                int y = h - padding - (int) (((double) pagosTiempo[i] / maxVal) * (h - padding * 2));
                if (prevX != -1) {
                    g2.drawLine(prevX, prevY, x, y);
                }
                g2.fillOval(x - 3, y - 3, 6, 6);
                prevX = x;
                prevY = y;
            }

            // Dibujar línea de Pagos en Mora (DANGER)
            g2.setColor(DANGER);
            prevX = -1; prevY = -1;
            for (int i = 0; i < n; i++) {
                int x = padding + i * step;
                int y = h - padding - (int) (((double) pagosMora[i] / maxVal) * (h - padding * 2));
                if (prevX != -1) {
                    g2.drawLine(prevX, prevY, x, y);
                }
                g2.fillOval(x - 3, y - 3, 6, 6);
                prevX = x;
                prevY = y;

                // Escribir mes en el eje
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.drawString(meses[i], x - 10, h - padding + 15);
                g2.setColor(DANGER);
            }
        }
    }

    // =========================================================================
    // EXPORTACIÓN A CSV CON EL PATRÓN ADAPTER
    // =========================================================================
    private void exportarReporteCSV() {
        showToast("Generando reporte financiero...", "info");

        // Simular retardo de exportación
        Timer exportTimer = new Timer(800, e -> {
            try {
                // Utilizando el adaptador (Adapter)
                ExportTarget adapter = new CopropietarioCSVAdapter(DataMock.getCopropietarios());

                File file = new File("AliGest_Reporte.csv");
                try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(file), StandardCharsets.UTF_8))) {
                    
                    // Escribir cabecera y contenido formateado
                    pw.print('\uFEFF'); // BOM de UTF-8
                    pw.print(adapter.getFormattedHeader());
                    pw.print(adapter.getFormattedContent());
                }

                showToast("Reporte descargado: AliGest_Reporte.csv", "success");
            } catch (Exception ex) {
                showToast("Error al exportar reporte.", "error");
                ex.printStackTrace();
            }
        });
        exportTimer.setRepeats(false);
        exportTimer.start();
    }

    // =========================================================================
    // UTILIDADES DE UI (METRICAS, TOASTS)
    // =========================================================================
    private void actualizarMetricas() {
        // Calcular en tiempo real
        double totalRecaudado = 0;
        double totalMora = 0;

        for (Copropietario c : DataMock.getCopropietarios()) {
            // Suponiendo recaudación en base a su alícuota por una expensa de $500 total
            double baseMonto = 500 * (c.getAlicuota() / 100.0);
            if ("Al Día".equals(c.getEstado())) {
                totalRecaudado += baseMonto;
            } else {
                totalMora += baseMonto * 1.12; // sumando mora recargo
            }
        }

        // Formatear
        lblRecaudacion.setText(String.format(Locale.US, "$%.2f", totalRecaudado));
        lblMorosidad.setText(String.format(Locale.US, "$%.2f", totalMora));
        
        int pendingSize = DataMock.getPagosPendientes().size();
        lblPagosPendientesCard.setText(String.valueOf(pendingSize));
        lblPagosPendientesBadge.setText(String.valueOf(pendingSize));
        lblPagosPendientesBadge.setVisible(pendingSize > 0);

        lblTotalPropietarios.setText(String.valueOf(DataMock.getCopropietarios().size()));
    }

    // --- SISTEMA DE TOASTS FLOTANTES APILADOS ---
    public void showToast(String message, String type) {
        JPanel toast = createToastPanel(message, type, null);
        addToast(toast);
        
        // Desaparecer después de 4 segundos
        Timer t = new Timer(4000, e -> removeToast(toast));
        t.setRepeats(false);
        t.start();
    }

    public void showToastWithUndo(String message, String type, Runnable undoAction) {
        JPanel toast = createToastPanel(message, type, undoAction);
        addToast(toast);

        // Desaparecer después de 7 segundos (más tiempo por tener botón interactivo)
        Timer t = new Timer(7000, e -> removeToast(toast));
        t.setRepeats(false);
        t.start();
    }

    private JPanel createToastPanel(String message, String type, Runnable undoAction) {
        JPanel toast = new JPanel(new BorderLayout(10, 0));
        
        Color bg = PRIMARY;
        String icon = "ℹ️ ";
        if ("success".equals(type)) {
            bg = SUCCESS;
            icon = "✅ ";
        } else if ("error".equals(type)) {
            bg = DANGER;
            icon = "❌ ";
        }

        toast.setBackground(bg);
        toast.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblMsg = new JLabel(icon + message);
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMsg.setForeground(Color.WHITE);
        toast.add(lblMsg, BorderLayout.CENTER);

        if (undoAction != null) {
            JButton btnUndo = new JButton("Deshacer");
            btnUndo.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnUndo.setForeground(PRIMARY);
            btnUndo.setBackground(Color.WHITE);
            btnUndo.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Color.WHITE, 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));
            btnUndo.setFocusPainted(false);
            btnUndo.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnUndo.addActionListener(e -> {
                undoAction.run();
                removeToast(toast);
            });
            toast.add(btnUndo, BorderLayout.EAST);
        }

        // Definir tamaño fijo
        toast.setSize(330, 50);
        return toast;
    }

    private synchronized void addToast(JPanel toast) {
        activeToasts.add(toast);
        getLayeredPane().add(toast, JLayeredPane.POPUP_LAYER);
        repositionToasts();
        getLayeredPane().repaint();
    }

    private synchronized void removeToast(JPanel toast) {
        if (activeToasts.contains(toast)) {
            activeToasts.remove(toast);
            getLayeredPane().remove(toast);
            repositionToasts();
            getLayeredPane().repaint();
        }
    }

    private synchronized void repositionToasts() {
        int y = getHeight() - 100;
        for (JPanel toast : activeToasts) {
            toast.setBounds(getWidth() - 360, y, 330, 48);
            y -= 58;
        }
    }
}
