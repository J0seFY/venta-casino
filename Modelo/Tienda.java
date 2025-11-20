package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Tienda {
    private List<Producto> productos;
    private List<Cliente> clientes;
    private List<Venta> ventas;

    public Tienda() {
        this.productos = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.ventas = new ArrayList<>();
    }

    public void cargarDatos() {
        // Cargar datos de ejemplo y persistirlos
        productos.clear();
        clientes.clear();
        ventas.clear();

        productos.add(new Producto("1", "Jugo", 500, 10));
        productos.add(new Producto("2", "Galleta", 300, 20));
        productos.add(new Producto("3", "Sandwich", 1500, 5));

        clientes.add(new Cliente("1-9", "Juan Perez"));
        clientes.add(new Estudiante("2-7", "Maria Lopez", 5000));

        // Persistencia delegada al nivel de aplicación/controlador
    }

    public Producto buscarProducto(String id) {
        for (Producto p : productos) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public Cliente buscarCliente(String rut) {
        for (Cliente c : clientes) {
            if (c.getRut().equals(rut)) {
                return c;
            }
        }
        return null;
    }

    public void registrarVenta(Venta venta) {
        ventas.add(venta);
        // Persistencia delegada al nivel de aplicación/controlador
    }

    public List<Venta> obtenerHistorial() {
        return new ArrayList<>(ventas);
    }

    public void agregarProducto(Producto p) {
        productos.add(p);
        // Persistencia delegada al nivel de aplicación/controlador
    }

    public void agregarCliente(Cliente c) {
        clientes.add(c);
        // Persistencia delegada al nivel de aplicación/controlador
    }

    public List<Producto> obtenerProductos() {
        return new ArrayList<>(productos);
    }

    public List<Cliente> obtenerClientes() {
        return new ArrayList<>(clientes);
    }

    // Métodos de configuración para cargar datos desde el exterior (controlador/servicio)
    public void setClientes(List<Cliente> clientes) {
        this.clientes = (clientes == null) ? new ArrayList<>() : new ArrayList<>(clientes);
    }

    public void setProductos(List<Producto> productos) {
        this.productos = (productos == null) ? new ArrayList<>() : new ArrayList<>(productos);
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = (ventas == null) ? new ArrayList<>() : new ArrayList<>(ventas);
    }
}
