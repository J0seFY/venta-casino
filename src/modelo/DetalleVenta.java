package modelo;

import java.io.Serializable;

public class DetalleVenta implements Serializable {
    private static final long serialVersionUID = 1L;

    private Producto producto;
    private int cantidad;
    private int subtotal;

    public DetalleVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = producto.calcularPrecio(cantidad);
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public int getSubtotal() { return subtotal; }
}