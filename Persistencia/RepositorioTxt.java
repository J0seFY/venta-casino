package Persistencia;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import Modelo.Cliente;
import Modelo.Estudiante;
import Modelo.Producto;
import Modelo.Venta;
import Modelo.DetalleVenta;

public class RepositorioTxt {
    private static final String DATA_DIR = "data";
    private static final String FILE_CLIENTES = "clientes.txt";
    private static final String FILE_PRODUCTOS = "productos.txt";
    private static final String FILE_VENTAS = "ventas.txt";

    private Path dir() throws IOException {
        Path d = Paths.get(DATA_DIR);
        if (!Files.exists(d)) Files.createDirectories(d);
        return d;
    }

    public List<Cliente> cargarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        try {
            Path path = dir().resolve(FILE_CLIENTES);
            if (!Files.exists(path)) return clientes;
            for (String line : Files.readAllLines(path)) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    String tipo = parts[0];
                    String rut = parts[1];
                    String nombre = parts[2];
                    if ("E".equalsIgnoreCase(tipo) && parts.length >= 4) {
                        try {
                            double saldo = Double.parseDouble(parts[3]);
                            clientes.add(new Estudiante(rut, nombre, saldo));
                        } catch (NumberFormatException ex) {
                            clientes.add(new Estudiante(rut, nombre, 0));
                        }
                    } else {
                        clientes.add(new Cliente(rut, nombre));
                    }
                }
            }
        } catch (IOException e) {
            // Silencioso: devolver lista vacía si hay error
        }
        return clientes;
    }

    public List<Producto> cargarProductos() {
        List<Producto> productos = new ArrayList<>();
        try {
            Path path = dir().resolve(FILE_PRODUCTOS);
            if (!Files.exists(path)) return productos;
            for (String line : Files.readAllLines(path)) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    String id = parts[0];
                    String nombre = parts[1];
                    try {
                        double precio = Double.parseDouble(parts[2]);
                        int stock = Integer.parseInt(parts[3]);
                        productos.add(new Producto(id, nombre, precio, stock));
                    } catch (NumberFormatException ex) {
                        // Saltar producto inválido
                    }
                }
            }
        } catch (IOException e) {
            // Silencioso
        }
        return productos;
    }

    public void guardarClientes(List<Cliente> clientes) {
        try {
            Path path = dir().resolve(FILE_CLIENTES);
            List<String> lines = new ArrayList<>();
            for (Cliente c : clientes) {
                if (c instanceof Estudiante) {
                    Estudiante e = (Estudiante) c;
                    lines.add(String.join(";",
                            "E", e.getRut(), e.getNombre(), String.valueOf(e.getSaldoBeca())));
                } else {
                    lines.add(String.join(";",
                            "C", c.getRut(), c.getNombre()));
                }
            }
            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            // Silencioso
        }
    }

    public void guardarProductos(List<Producto> productos) {
        try {
            Path path = dir().resolve(FILE_PRODUCTOS);
            List<String> lines = new ArrayList<>();
            for (Producto p : productos) {
                lines.add(String.join(";",
                        p.getId(), p.getNombre(), String.valueOf(p.getPrecio()), String.valueOf(p.getStock())));
            }
            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            // Silencioso
        }
    }

    public List<Venta> cargarVentas(List<Cliente> clientes, List<Producto> productos) {
        List<Venta> ventas = new ArrayList<>();
        try {
            Path path = dir().resolve(FILE_VENTAS);
            if (!Files.exists(path)) return ventas;
            Cliente currentCliente = null;
            int currentId = -1;
            List<DetalleVenta> currentDetalles = null;

            for (String line : Files.readAllLines(path)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    if (currentCliente != null && currentDetalles != null) {
                        ventas.add(new Venta(currentId, currentCliente, currentDetalles));
                    }
                    currentCliente = null;
                    currentDetalles = null;
                    currentId = -1;
                    continue;
                }
                String[] parts = trimmed.split(";");
                if (parts.length == 0) continue;
                if ("V".equalsIgnoreCase(parts[0]) && parts.length >= 3) {
                    // flush previous
                    if (currentCliente != null && currentDetalles != null) {
                        ventas.add(new Venta(currentId, currentCliente, currentDetalles));
                    }
                    try {
                        currentId = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        currentId = -1;
                    }
                    String rut = parts[2];
                    currentCliente = buscarClientePorRut(clientes, rut);
                    currentDetalles = new ArrayList<>();
                } else if ("D".equalsIgnoreCase(parts[0]) && parts.length >= 4) {
                    if (currentCliente == null || currentDetalles == null) continue;
                    String prodId = parts[1];
                    Producto p = buscarProductoPorId(productos, prodId);
                    if (p == null) continue;
                    try {
                        int cantidad = Integer.parseInt(parts[2]);
                        double precioUnit = Double.parseDouble(parts[3]);
                        currentDetalles.add(new DetalleVenta(p, cantidad, precioUnit));
                    } catch (NumberFormatException e) {
                        // saltar detalle inválido
                    }
                }
            }
            // flush last
            if (currentCliente != null && currentDetalles != null) {
                ventas.add(new Venta(currentId, currentCliente, currentDetalles));
            }
        } catch (IOException e) {
            // Silencioso
        }
        return ventas;
    }

    public void guardarVentas(List<Venta> ventas) {
        try {
            Path path = dir().resolve(FILE_VENTAS);
            List<String> lines = new ArrayList<>();
            for (Venta v : ventas) {
                lines.add(String.join(";", "V", String.valueOf(v.getIdVenta()), v.getCliente().getRut()));
                for (DetalleVenta d : v.getDetalles()) {
                    lines.add(String.join(";",
                            "D",
                            d.getProducto().getId(),
                            String.valueOf(d.getCantidad()),
                            String.valueOf(d.getPrecioUnitario())));
                }
                lines.add("");
            }
            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            // Silencioso
        }
    }

    private Cliente buscarClientePorRut(List<Cliente> clientes, String rut) {
        for (Cliente c : clientes) {
            if (c.getRut().equals(rut)) return c;
        }
        return null;
    }

    private Producto buscarProductoPorId(List<Producto> productos, String id) {
        for (Producto p : productos) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }
}
