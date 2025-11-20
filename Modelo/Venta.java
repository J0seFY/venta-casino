package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Venta {
    private static int contadorVentas = 0;
    private int idVenta;
    private Cliente cliente;
    private List<DetalleVenta> detalles;

    public Venta(Cliente cliente) {
        this.idVenta = ++contadorVentas;
        this.cliente = cliente;
        this.detalles = new ArrayList<>();
    }

    public Venta(int idVenta, Cliente cliente, List<DetalleVenta> detalles) {
        this.idVenta = idVenta;
        this.cliente = cliente;
        this.detalles = new ArrayList<>(detalles);
        if (idVenta > contadorVentas) {
            contadorVentas = idVenta;
        }
    }

    public void agregarDetalle(Producto p, int cantidad) {
        if (p.tieneStock(cantidad)) {
            detalles.add(new DetalleVenta(p, cantidad));
            p.descontarStock(cantidad);
        }
    }

    public double calcularTotal() {
        double total = 0;
        for (DetalleVenta detalle : detalles) {
            total += detalle.calcularSubtotal();
        }
        return total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }
}
