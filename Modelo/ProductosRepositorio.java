package Modelo;

import java.util.List;

public interface ProductosRepositorio {
    List<Producto> cargarProductos();
    void guardarProductos(List<Producto> productos);
}
