package modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Venta implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String fecha;
    private Cliente cliente;
    private List<DetalleVenta> detalles;
    private String metodoPago;
    private int total;
    private int vuelto;

    public Venta(long id, Cliente cliente, List<DetalleVenta> detalles, String metodoPago, int total) {
        this.id = id;
        this.fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        this.cliente = cliente;
        this.detalles = detalles;
        this.metodoPago = metodoPago;
        this.total = total;
        this.vuelto = 0;
    }

    public void setVuelto(int vuelto) { this.vuelto = vuelto; }

    public long getId() { return id; }
    public String getFecha() { return fecha; }
    public Cliente getCliente() { return cliente; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public String getMetodoPago() { return metodoPago; }
    public int getTotal() { return total; }
    public int getVuelto() { return vuelto; }
}