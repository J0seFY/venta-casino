package vista;

import controlador.ControladorCasino;
import modelo.Producto;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelInventario extends JPanel {
    private ControladorCasino controlador;
    private VistaPrincipal mainFrame;
    private DefaultTableModel modelo;

    public PanelInventario(ControladorCasino ctrl, VistaPrincipal frame) {
        this.controlador = ctrl;
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnVolver = new JButton("< Volver");
        btnVolver.addActionListener(e -> mainFrame.mostrarVista("MENU"));
        add(btnVolver, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Precio", "Stock"}, 0);
        JTable tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnAgregar = new JButton("Nuevo Producto");
        JButton btnStock = new JButton("Aumentar Stock");

        btnAgregar.addActionListener(e -> agregarProducto());
        btnStock.addActionListener(e -> aumentarStock());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnStock);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void actualizarTabla() {
        modelo.setRowCount(0);
        for (Producto p : controlador.obtenerProductos()) {
            modelo.addRow(new Object[]{p.getId(), p.getNombre(), p.getPrecio(), p.getStock()});
        }
    }

    private void agregarProducto() {
        JTextField txtId = new JTextField();
        JTextField txtNom = new JTextField();
        JTextField txtPre = new JTextField();
        JTextField txtStk = new JTextField();
        Object[] msg = {"ID:", txtId, "Nombre:", txtNom, "Precio:", txtPre, "Stock:", txtStk};

        if (JOptionPane.showConfirmDialog(this, msg, "Nuevo Producto", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                controlador.agregarProductoNuevo(txtId.getText(), txtNom.getText(),
                        Integer.parseInt(txtPre.getText()), Integer.parseInt(txtStk.getText()));
                actualizarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datos inválidos");
            }
        }
    }

    private void aumentarStock() {
        String id = JOptionPane.showInputDialog("ID del producto:");
        if (id != null) {
            String cant = JOptionPane.showInputDialog("Cantidad a agregar:");
            try {
                controlador.aumentarStock(id, Integer.parseInt(cant));
                actualizarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar stock");
            }
        }
    }
}