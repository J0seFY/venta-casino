package Modelo;

import java.util.ArrayList;
import java.util.List;
import Persistencia.RepositorioTxt;

public class Tienda {
    private List<Producto> productos;
    private List<Cliente> clientes;
    private List<Venta> ventas;
    private RepositorioTxt repo;

    public Tienda() {
        this.productos = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.ventas = new ArrayList<>();
        this.repo = new RepositorioTxt();
        cargarDesdeArchivos();
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

        repo.guardarProductos(productos);
        repo.guardarClientes(clientes);
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
        // Persistir cambios en clientes (beca) y productos (stock), y ventas
        repo.guardarClientes(clientes);
        repo.guardarProductos(productos);
        repo.guardarVentas(ventas);
    }

    public List<Venta> obtenerHistorial() {
        return new ArrayList<>(ventas);
    }

    public void agregarProducto(Producto p) {
        productos.add(p);
        repo.guardarProductos(productos);
    }

    public void agregarCliente(Cliente c) {
        clientes.add(c);
        repo.guardarClientes(clientes);
    }

    public List<Producto> obtenerProductos() {
        return new ArrayList<>(productos);
    }

    public List<Cliente> obtenerClientes() {
        return new ArrayList<>(clientes);
    }

    public void cargarDesdeArchivos() {
        List<Cliente> cls = repo.cargarClientes();
        List<Producto> prs = repo.cargarProductos();
        this.clientes = (cls == null) ? new ArrayList<>() : new ArrayList<>(cls);
        this.productos = (prs == null) ? new ArrayList<>() : new ArrayList<>(prs);
        List<Venta> vts = repo.cargarVentas(this.clientes, this.productos);
        this.ventas = (vts == null) ? new ArrayList<>() : new ArrayList<>(vts);
    }
}
