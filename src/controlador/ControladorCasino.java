package controlador;

import modelo.*; // Importar todas las clases del modelo
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorCasino {
    private static final String ARCHIVO_DATOS = "datos_casino.bin";

    private List<Producto> productos;
    private List<Cliente> clientes;
    private List<Venta> ventas;
    private List<DetalleVenta> carritoActual;

    public ControladorCasino() {
        this.carritoActual = new ArrayList<>();
        cargarDatosPersistentes();
    }

    @SuppressWarnings("unchecked")
    private void cargarDatosPersistentes() {
        File archivo = new File(ARCHIVO_DATOS);
        if (archivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                productos = (List<Producto>) ois.readObject();
                clientes = (List<Cliente>) ois.readObject();
                ventas = (List<Venta>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                inicializarDatosPorDefecto();
            }
        } else {
            inicializarDatosPorDefecto();
        }
    }

    private void guardarDatosPersistentes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_DATOS))) {
            oos.writeObject(productos);
            oos.writeObject(clientes);
            oos.writeObject(ventas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inicializarDatosPorDefecto() {
        productos = new ArrayList<>();
        clientes = new ArrayList<>();
        ventas = new ArrayList<>();

        productos.add(new Producto("101", "Bebida Lata", 1200, 50));
        productos.add(new Producto("102", "Sándwich Ave Palta", 2500, 20));
        productos.add(new Producto("103", "Fajita Pollo", 2800, 15));
        productos.add(new Producto("104", "Galleta Chocolate", 500, 100));

        guardarDatosPersistentes();
    }

    public List<Producto> obtenerProductos() { return productos; }

    public List<Producto> buscarProductos(String consulta) {
        String lower = consulta.toLowerCase();
        return productos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(lower) || p.getId().contains(lower))
                .collect(Collectors.toList());
    }

    public Producto buscarProductoPorId(String id) {
        return productos.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    public void agregarProductoNuevo(String id, String nombre, int precio, int stock) {
        productos.add(new Producto(id, nombre, precio, stock));
        guardarDatosPersistentes();
    }

    public void aumentarStock(String id, int cantidad) {
        Producto p = buscarProductoPorId(id);
        if (p != null) {
            p.agregarStock(cantidad);
            guardarDatosPersistentes();
        }
    }

    public void agregarAlCarrito(Producto p, int cantidad) throws Exception {
        if (p.getStock() < cantidad) throw new Exception("Stock insuficiente.");

        boolean encontrado = false;
        for (DetalleVenta d : carritoActual) {
            if (d.getProducto().getId().equals(p.getId())) {
                if (d.getCantidad() + cantidad > p.getStock()) throw new Exception("Stock insuficiente.");
                int nuevaCant = d.getCantidad() + cantidad;
                carritoActual.remove(d);
                carritoActual.add(new DetalleVenta(p, nuevaCant));
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            carritoActual.add(new DetalleVenta(p, cantidad));
        }
    }

    public void vaciarCarrito() { carritoActual.clear(); }
    public List<DetalleVenta> getCarrito() { return carritoActual; }

    public int calcularTotalCarrito() {
        return carritoActual.stream().mapToInt(DetalleVenta::getSubtotal).sum();
    }

    public Cliente buscarCliente(String rut) {
        return clientes.stream().filter(c -> c.getRut().equals(rut)).findFirst().orElse(null);
    }

    public Venta finalizarVenta(String rut, String nombreCliente, String metodoPago,
                                int montoEfectivo, String numTarjeta, String claveTarjeta, String codigoJunaeb) throws Exception {

        if (carritoActual.isEmpty()) throw new Exception("El carrito está vacío.");

        Cliente cliente = buscarCliente(rut);
        if (cliente == null) {
            cliente = new Cliente(rut, nombreCliente);
            if (metodoPago.equals("JUNAEB")) {
                cliente.setSaldoJunaeb(48000);
            }
            clientes.add(cliente);
        }

        int total = calcularTotalCarrito();
        int vuelto = 0;

        if (metodoPago.equals("EFECTIVO")) {
            if (montoEfectivo < total) throw new Exception("Dinero insuficiente.");
            vuelto = montoEfectivo - total;
        } else if (metodoPago.equals("TARJETA")) {
            if (numTarjeta.isEmpty() || claveTarjeta.isEmpty()) throw new Exception("Datos inválidos.");
        } else if (metodoPago.equals("JUNAEB")) {
            if (codigoJunaeb.length() != 6) throw new Exception("Código debe ser 6 dígitos.");
            if (!cliente.esEstudianteJunaeb()) {
                cliente.setSaldoJunaeb(48000);
            }
            if (cliente.getSaldoJunaeb() < total) {
                throw new Exception("Saldo insuficiente: " + cliente.getSaldoJunaeb());
            }
            cliente.setSaldoJunaeb(cliente.getSaldoJunaeb() - total);
        }

        for (DetalleVenta d : carritoActual) {
            d.getProducto().disminuirStock(d.getCantidad());
        }

        Venta nuevaVenta = new Venta(System.currentTimeMillis(), cliente, new ArrayList<>(carritoActual), metodoPago, total);
        if (metodoPago.equals("EFECTIVO")) nuevaVenta.setVuelto(vuelto);

        ventas.add(nuevaVenta);
        vaciarCarrito();
        guardarDatosPersistentes();

        return nuevaVenta;
    }

    public List<Venta> obtenerHistorialVentas() { return ventas; }
}