package Vista;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import Modelo.DetalleVenta;
import Modelo.Cliente;
import Modelo.Estudiante;
import Modelo.Producto;
import Modelo.Venta;

public class VistaConsola {
    private Scanner scanner;

    public VistaConsola() {
        this.scanner = new Scanner(System.in);
    }

    public int mostrarMenuPrincipal() {
        System.out.println("\n=== SISTEMA DE VENTAS - CASINO UNIVERSITARIO ===");
        System.out.println("1. Gestionar clientes");
        System.out.println("2. Gestionar productos");
        System.out.println("3. Iniciar nueva venta");
        System.out.println("4. Ver historial de ventas");
        System.out.println("9. Cargar datos de ejemplo");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
        return leerEnteroSeguro();
    }

    public int mostrarMenuClientes() {
        System.out.println("\n--- CLIENTES ---");
        System.out.println("1. Listar clientes");
        System.out.println("2. Crear cliente");
        System.out.println("3. Crear estudiante");
        System.out.println("4. Buscar cliente por RUT");
        System.out.println("0. Volver");
        System.out.print("Seleccione una opción: ");
        return leerEnteroSeguro();
    }

    public int mostrarMenuProductos() {
        System.out.println("\n--- PRODUCTOS ---");
        System.out.println("1. Listar productos");
        System.out.println("2. Agregar producto");
        System.out.println("3. Buscar producto por ID");
        System.out.println("0. Volver");
        System.out.print("Seleccione una opción: ");
        return leerEnteroSeguro();
    }

    public String pedirRutCliente() {
        System.out.print("Ingrese RUT del cliente: ");
        return leerLinea();
    }

    public String pedirIdProducto() {
        System.out.print("Ingrese ID del producto (o 'fin' para terminar): ");
        return leerLinea();
    }

    public int pedirCantidad() {
        System.out.print("Ingrese la cantidad: ");
        return leerEnteroSeguro();
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarError(String mensaje) {
        System.err.println("ERROR: " + mensaje);
    }

    public void mostrarVenta(Venta venta) {
        System.out.println("\n--- DETALLE DE VENTA ---");
        System.out.println("ID Venta: " + venta.getIdVenta());
        System.out.println("Cliente: " + venta.getCliente().getNombre() + " (" + venta.getCliente().getRut() + ")");
        System.out.println("--------------------------");
        for (DetalleVenta detalle : venta.getDetalles()) {
            System.out.printf("%-15s x%d\t$%.2f\n",
                    detalle.getProducto().getNombre(),
                    detalle.getCantidad(),
                    detalle.calcularSubtotal());
        }
        System.out.println("--------------------------");
        System.out.printf("TOTAL: $%.2f\n", venta.calcularTotal());
    }

    public void mostrarHistorial(List<Venta> ventas) {
        System.out.println("\n--- HISTORIAL DE VENTAS ---");
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas.");
        } else {
            for (Venta venta : ventas) {
                mostrarVenta(venta);
            }
        }
    }

    public void listarClientes(List<Cliente> clientes) {
        System.out.println("\n--- CLIENTES ---");
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes.");
            return;
        }
        for (Cliente c : clientes) {
            if (c instanceof Estudiante) {
                Estudiante e = (Estudiante) c;
                System.out.printf("%s - %s (Estudiante, Beca: $%.2f)\n", e.getRut(), e.getNombre(), e.getSaldoBeca());
            } else {
                System.out.printf("%s - %s\n", c.getRut(), c.getNombre());
            }
        }
    }

    public void listarProductos(List<Producto> productos) {
        System.out.println("\n--- PRODUCTOS ---");
        if (productos.isEmpty()) {
            System.out.println("No hay productos.");
            return;
        }
        for (Producto p : productos) {
            System.out.printf("%s - %s $%.2f (Stock: %d)\n", p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
        }
    }

    public Cliente pedirDatosCliente() {
        // Obsoleto: usar pedirRutCliente() y pedirNombre() desde el controlador
        System.out.print("RUT: ");
        String rut = leerLinea();
        System.out.print("Nombre: ");
        String nombre = leerLinea();
        if (rut.isEmpty() || nombre.isEmpty()) return null;
        return new Cliente(rut, nombre);
    }

    public Cliente pedirDatosClienteConRut(String rut) {
        // Obsoleto: usar pedirNombre() desde el controlador
        System.out.print("Nombre: ");
        String nombre = leerLinea();
        if (nombre.isEmpty()) return null;
        return new Cliente(rut, nombre);
    }

    public Estudiante pedirDatosEstudiante() {
        // Obsoleto: usar pedirRutCliente(), pedirNombre() y pedirSaldoBeca()
        System.out.print("RUT: ");
        String rut = leerLinea();
        System.out.print("Nombre: ");
        String nombre = leerLinea();
        System.out.print("Saldo beca: ");
        double saldo = leerDoubleSeguro();
        return new Estudiante(rut, nombre, saldo);
    }

    public Estudiante pedirDatosEstudianteConRut(String rut) {
        // Obsoleto: usar pedirNombre() y pedirSaldoBeca()
        System.out.print("Nombre: ");
        String nombre = leerLinea();
        System.out.print("Saldo beca: ");
        double saldo = leerDoubleSeguro();
        return new Estudiante(rut, nombre, saldo);
    }

    public Producto pedirDatosProducto() {
        // Obsoleto: usar pedirIdProducto(), pedirNombreProducto(), pedirPrecioProducto(), pedirStockProducto()
        System.out.print("ID: ");
        String id = leerLinea();
        System.out.print("Nombre: ");
        String nombre = leerLinea();
        System.out.print("Precio: ");
        double precio = leerDoubleSeguro();
        System.out.print("Stock: ");
        int stock = leerEnteroSeguro();
        return new Producto(id, nombre, precio, stock);
    }

    // Nuevos métodos primitivos para una vista más pasiva
    public String pedirNombre() {
        System.out.print("Nombre: ");
        return leerLinea();
    }

    public double pedirSaldoBeca() {
        System.out.print("Saldo beca: ");
        return leerDoubleSeguro();
    }

    public String pedirNombreProducto() {
        System.out.print("Nombre: ");
        return leerLinea();
    }

    public double pedirPrecioProducto() {
        System.out.print("Precio: ");
        return leerDoubleSeguro();
    }

    public int pedirStockProducto() {
        System.out.print("Stock: ");
        return leerEnteroSeguro();
    }

    public boolean confirmar(String mensaje) {
        System.out.print(mensaje);
        String r = leerLinea().trim().toLowerCase();
        return r.equals("s") || r.equals("si") || r.equals("sí");
    }

    public int seleccionarMetodoPago() {
        System.out.println("\nMétodo de pago:") ;
        System.out.println("1. Efectivo");
        System.out.println("2. Beca estudiante");
        System.out.print("Seleccione una opción: ");
        int op = leerEnteroSeguro();
        return (op == 2) ? 2 : 1;
    }

    private int leerEnteroSeguro() {
        while (true) {
            try {
                int v = Integer.parseInt(leerLinea());
                return v;
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Intente nuevamente: ");
            }
        }
    }

    private double leerDoubleSeguro() {
        while (true) {
            try {
                double v = Double.parseDouble(leerLinea().replace(",", "."));
                return v;
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Intente nuevamente: ");
            }
        }
    }

    private String leerLinea() {
        try {
            if (!scanner.hasNextLine()) return "";
            String s = scanner.nextLine();
            return s == null ? "" : s.trim();
        } catch (Exception e) {
            return "";
        }
    }
}
