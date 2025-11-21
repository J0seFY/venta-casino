package vista;

import controlador.ControladorCasino;
import modelo.*; // Importar modelos para usar Cliente, Producto, etc.
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class PanelVenta extends JPanel {
    private ControladorCasino controlador;
    private VistaPrincipal mainFrame;

    private JTextField txtBuscar;
    private DefaultTableModel modeloTablaProd;
    private DefaultTableModel modeloTablaCarrito;
    private JLabel lblTotal;
    private JTable tablaProductos;

    public PanelVenta(ControladorCasino ctrl, VistaPrincipal frame) {
        this.controlador = ctrl;
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- TOP: Volver y Buscador ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton btnVolver = new JButton("< Volver");
        btnVolver.addActionListener(e -> {
            controlador.vaciarCarrito();
            actualizarCarritoUI();
            mainFrame.mostrarVista("MENU");
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscar = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarProductos());
        txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { buscarProductos(); }
        });
        searchPanel.add(new JLabel("Buscar Producto (ID/Nombre): "));
        searchPanel.add(txtBuscar);
        searchPanel.add(btnBuscar);

        topPanel.add(btnVolver, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER: Listas de Productos y Carrito ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Izquierda: Productos Disponibles
        modeloTablaProd = new DefaultTableModel(new String[]{"ID", "Nombre", "Precio", "Stock"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaProductos = new JTable(modeloTablaProd);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollProd = new JScrollPane(tablaProductos);
        scrollProd.setBorder(BorderFactory.createTitledBorder("Productos Disponibles (Doble Click para agregar)"));

        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = tablaProductos.getSelectedRow();
                    if (row != -1) {
                        String id = (String) modeloTablaProd.getValueAt(row, 0);
                        agregarAlCarrito(id);
                    }
                }
            }
        });

        // Derecha: Carrito
        modeloTablaCarrito = new DefaultTableModel(new String[]{"Producto", "Cant", "Subtotal"}, 0);
        JTable tablaCarrito = new JTable(modeloTablaCarrito);
        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);
        scrollCarrito.setBorder(BorderFactory.createTitledBorder("Carrito de Compras"));

        centerPanel.add(scrollProd);
        centerPanel.add(scrollCarrito);
        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM: Totales y Pagar ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: $0");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 20));
        JButton btnPagar = new JButton("Finalizar Compra");
        btnPagar.setFont(new Font("Arial", Font.BOLD, 14));
        btnPagar.setBackground(new Color(100, 200, 100));
        btnPagar.addActionListener(e -> iniciarProcesoPago());

        bottomPanel.add(lblTotal);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(btnPagar);
        add(bottomPanel, BorderLayout.SOUTH);

        // Carga inicial
        buscarProductos();
    }

    private void buscarProductos() {
        modeloTablaProd.setRowCount(0);
        List<Producto> lista = controlador.buscarProductos(txtBuscar.getText());
        for (Producto p : lista) {
            modeloTablaProd.addRow(new Object[]{p.getId(), p.getNombre(), p.getPrecio(), p.getStock()});
        }
    }

    private void agregarAlCarrito(String idProd) {
        Producto p = controlador.buscarProductoPorId(idProd);
        if (p != null) {
            try {
                controlador.agregarAlCarrito(p, 1);
                actualizarCarritoUI();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualizarCarritoUI() {
        modeloTablaCarrito.setRowCount(0);
        for (DetalleVenta dv : controlador.getCarrito()) {
            modeloTablaCarrito.addRow(new Object[]{dv.getProducto().getNombre(), dv.getCantidad(), dv.getSubtotal()});
        }
        lblTotal.setText("Total: $" + controlador.calcularTotalCarrito());
    }

    private void iniciarProcesoPago() {
        if (controlador.getCarrito().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío");
            return;
        }

        // 1. Pedir RUT
        String rut = JOptionPane.showInputDialog(this, "Ingrese RUT del Cliente:");
        if (rut == null || rut.trim().isEmpty()) return;

        // 2. Verificar Cliente
        Cliente c = controlador.buscarCliente(rut);
        String nombre = "";
        if (c != null) {
            JOptionPane.showMessageDialog(this, "Cliente encontrado: " + c.getNombre());
            nombre = c.getNombre();
        } else {
            nombre = JOptionPane.showInputDialog(this, "Cliente nuevo. Ingrese Nombre:");
            if (nombre == null || nombre.trim().isEmpty()) return;
        }

        // 3. Seleccionar Método de Pago
        String[] opciones = {"EFECTIVO", "TARJETA", "JUNAEB"};
        int seleccion = JOptionPane.showOptionDialog(this, "Seleccione Método de Pago", "Pago",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion == -1) return;
        String metodo = opciones[seleccion];

        // 4. Datos específicos del pago
        int efectivo = 0;
        String tarjetaNum = "", tarjetaClave = "", junaebCode = "";

        if (metodo.equals("EFECTIVO")) {
            String montoStr = JOptionPane.showInputDialog("Total a pagar: $" + controlador.calcularTotalCarrito() + "\nIngrese monto entregado:");
            try { efectivo = Integer.parseInt(montoStr); } catch(Exception ex) { return; }
        } else if (metodo.equals("TARJETA")) {
            JTextField txtNum = new JTextField();
            JPasswordField txtPass = new JPasswordField();
            Object[] message = {"Número Tarjeta:", txtNum, "Clave:", txtPass};
            int option = JOptionPane.showConfirmDialog(this, message, "Datos Tarjeta", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                tarjetaNum = txtNum.getText();
                tarjetaClave = new String(txtPass.getPassword());
            } else return;
        } else if (metodo.equals("JUNAEB")) {
            junaebCode = JOptionPane.showInputDialog("Ingrese código Junaeb (6 dígitos):");
            if (junaebCode == null) return;
        }

        // 5. Finalizar
        try {
            Venta v = controlador.finalizarVenta(rut, nombre, metodo, efectivo, tarjetaNum, tarjetaClave, junaebCode);
            mostrarBoleta(v);
            actualizarCarritoUI();
            buscarProductos(); // Refrescar stock en tabla
            mainFrame.mostrarVista("MENU");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en la venta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarBoleta(Venta v) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== BOLETA ELECTRÓNICA ===\n");
        sb.append("Fecha: ").append(v.getFecha()).append("\n");
        sb.append("Cliente: ").append(v.getCliente().getNombre()).append("\n");
        sb.append("RUT: ").append(v.getCliente().getRut()).append("\n");
        sb.append("--------------------------\n");
        for (DetalleVenta d : v.getDetalles()) {
            sb.append(d.getCantidad()).append(" x ").append(d.getProducto().getNombre())
                    .append(" $").append(d.getSubtotal()).append("\n");
        }
        sb.append("--------------------------\n");
        sb.append("TOTAL: $").append(v.getTotal()).append("\n");
        sb.append("Pago: ").append(v.getMetodoPago()).append("\n");

        if (v.getMetodoPago().equals("EFECTIVO")) {
            sb.append("Vuelto: $").append(v.getVuelto()).append("\n");
        } else if (v.getMetodoPago().equals("JUNAEB")) {
            sb.append("Saldo Restante: $").append(v.getCliente().getSaldoJunaeb()).append("\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Boleta Generada", JOptionPane.INFORMATION_MESSAGE);
    }
}