package vista;

import controlador.ControladorCasino;
import modelo.Venta;
import modelo.DetalleVenta;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelHistorial extends JPanel {
    private ControladorCasino controlador;
    private VistaPrincipal mainFrame;
    private DefaultTableModel modelo;

    public PanelHistorial(ControladorCasino ctrl, VistaPrincipal frame) {
        this.controlador = ctrl;
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnVolver = new JButton("< Volver");
        btnVolver.addActionListener(e -> mainFrame.mostrarVista("MENU"));
        add(btnVolver, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID Venta", "Fecha", "Cliente", "Total", "Pago"}, 0);
        JTable tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = tabla.getSelectedRow();
                    if(row != -1) mostrarDetalleVenta(row);
                }
            }
        });
    }

    public void actualizarTabla() {
        modelo.setRowCount(0);
        for (Venta v : controlador.obtenerHistorialVentas()) {
            modelo.addRow(new Object[]{v.getId(), v.getFecha(), v.getCliente().getNombre(), v.getTotal(), v.getMetodoPago()});
        }
    }

    private void mostrarDetalleVenta(int rowIndex) {
        Venta v = controlador.obtenerHistorialVentas().get(rowIndex);
        StringBuilder sb = new StringBuilder();
        sb.append("Productos:\n");
        for(DetalleVenta d : v.getDetalles()) {
            sb.append("- ").append(d.getProducto().getNombre()).append(" x").append(d.getCantidad()).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Detalle Venta #" + v.getId(), JOptionPane.INFORMATION_MESSAGE);
    }
}