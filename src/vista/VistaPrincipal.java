package vista;

import controlador.ControladorCasino;
import javax.swing.*;
import java.awt.*;

public class VistaPrincipal extends JFrame {
    private ControladorCasino controlador;
    private JPanel panelContenido;
    private CardLayout cardLayout;

    public VistaPrincipal(ControladorCasino controlador) {
        this.controlador = controlador;
        inicializarUI();
    }

    private void inicializarUI() {
        setTitle("Sistema Casino Universitario");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);

        // Paneles (Deben estar creados en el paquete vista)
        JPanel panelMenu = crearPanelMenu();
        // Nota: Asegurate de crear los archivos PanelVenta.java, etc. en el paquete vista
        // Si no quieres crear archivos separados para paneles, deberás definirlos aquí como clases internas.
        // Para este ejemplo asumo que crearás los archivos separados o pegarás el código de los paneles abajo.
        JPanel panelVenta = new PanelVenta(controlador, this);
        JPanel panelHistorial = new PanelHistorial(controlador, this);
        JPanel panelInventario = new PanelInventario(controlador, this);

        panelContenido.add(panelMenu, "MENU");
        panelContenido.add(panelVenta, "VENTA");
        panelContenido.add(panelHistorial, "HISTORIAL");
        panelContenido.add(panelInventario, "INVENTARIO");

        add(panelContenido);
    }

    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Menú Principal", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(titulo, gbc);

        JButton btnVenta = crearBotonMenu("Iniciar Venta");
        btnVenta.addActionListener(e -> mostrarVista("VENTA"));
        gbc.gridy = 1; panel.add(btnVenta, gbc);

        JButton btnHistorial = crearBotonMenu("Listar Ventas");
        btnHistorial.addActionListener(e -> {
            ((PanelHistorial)panelContenido.getComponent(2)).actualizarTabla();
            mostrarVista("HISTORIAL");
        });
        gbc.gridy = 2; panel.add(btnHistorial, gbc);

        JButton btnInventario = crearBotonMenu("Gestión Inventario");
        btnInventario.addActionListener(e -> {
            ((PanelInventario)panelContenido.getComponent(3)).actualizarTabla();
            mostrarVista("INVENTARIO");
        });
        gbc.gridy = 3; panel.add(btnInventario, gbc);

        JButton btnSalir = crearBotonMenu("Salir");
        btnSalir.setBackground(new Color(255, 100, 100));
        btnSalir.addActionListener(e -> System.exit(0));
        gbc.gridy = 4; panel.add(btnSalir, gbc);

        return panel;
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.setPreferredSize(new Dimension(250, 50));
        return btn;
    }

    public void mostrarVista(String nombreVista) {
        cardLayout.show(panelContenido, nombreVista);
    }
}