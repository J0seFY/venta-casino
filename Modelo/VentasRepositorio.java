package Modelo;

import java.util.List;

public interface VentasRepositorio {
    List<Venta> cargarVentas(List<Cliente> clientes, List<Producto> productos);
    void guardarVentas(List<Venta> ventas);
}
