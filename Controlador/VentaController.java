package Controlador;

import Modelo.Cliente;
import Modelo.Estudiante;
import Modelo.Producto;
import Modelo.Tienda;
import Modelo.Venta;
import Vista.VistaConsola;

public class VentaController {
    private Tienda tienda;
    private VistaConsola vista;
    private Venta ventaActual;

    public VentaController(Tienda tienda, VistaConsola vista) {
        this.tienda = tienda;
        this.vista = vista;
    }

    public void iniciar() {
        tienda.cargarDesdeArchivos();
        int opcion;
        do {
            opcion = vista.mostrarMenuPrincipal();
            switch (opcion) {
                case 1:
                    gestionarClientes();
                    break;
                case 2:
                    gestionarProductos();
                    break;
                case 3:
                    iniciarVenta();
                    break;
                case 4:
                    vista.mostrarHistorial(tienda.obtenerHistorial());
                    break;
                case 9:
                    tienda.cargarDatos();
                    vista.mostrarMensaje("Datos de ejemplo cargados.");
                    break;
                case 0:
                    vista.mostrarMensaje("Saliendo del sistema...");
                    break;
                default:
                    vista.mostrarError("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void gestionarClientes() {
        int opcion;
        do {
            opcion = vista.mostrarMenuClientes();
            switch (opcion) {
                case 1:
                    vista.listarClientes(tienda.obtenerClientes());
                    break;
                case 2: {
                    Cliente c = vista.pedirDatosCliente();
                    if (c != null) {
                        if (tienda.buscarCliente(c.getRut()) != null) {
                            vista.mostrarError("Ya existe un cliente con ese RUT.");
                        } else {
                            tienda.agregarCliente(c);
                            vista.mostrarMensaje("Cliente creado.");
                        }
                    }
                    break;
                }
                case 3: {
                    Estudiante e = vista.pedirDatosEstudiante();
                    if (e != null) {
                        if (tienda.buscarCliente(e.getRut()) != null) {
                            vista.mostrarError("Ya existe un cliente con ese RUT.");
                        } else {
                            tienda.agregarCliente(e);
                            vista.mostrarMensaje("Estudiante creado.");
                        }
                    }
                    break;
                }
                case 4: {
                    String rut = vista.pedirRutCliente();
                    Cliente c = tienda.buscarCliente(rut);
                    if (c == null) vista.mostrarError("Cliente no encontrado.");
                    else vista.mostrarMensaje("Encontrado: " + c.getNombre() + " (" + c.getRut() + ")");
                    break;
                }
                case 0:
                    break;
                default:
                    vista.mostrarError("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void gestionarProductos() {
        int opcion;
        do {
            opcion = vista.mostrarMenuProductos();
            switch (opcion) {
                case 1:
                    vista.listarProductos(tienda.obtenerProductos());
                    break;
                case 2: {
                    Producto p = vista.pedirDatosProducto();
                    if (p != null) {
                        if (tienda.buscarProducto(p.getId()) != null) {
                            vista.mostrarError("Ya existe un producto con ese ID.");
                        } else {
                            tienda.agregarProducto(p);
                            vista.mostrarMensaje("Producto agregado.");
                        }
                    }
                    break;
                }
                case 3: {
                    String id = vista.pedirIdProducto();
                    if (id.equalsIgnoreCase("fin")) break;
                    Producto p = tienda.buscarProducto(id);
                    if (p == null) vista.mostrarError("Producto no encontrado.");
                    else vista.mostrarMensaje("Encontrado: " + p.getNombre() + " ($" + p.getPrecio() + ") Stock: " + p.getStock());
                    break;
                }
                case 0:
                    break;
                default:
                    vista.mostrarError("Opción no válida.");
            }
        } while (opcion != 0);
    }

    public void iniciarVenta() {
        String rut = vista.pedirRutCliente();
        Cliente cliente = tienda.buscarCliente(rut);
        if (cliente == null) {
            boolean crear = vista.confirmar("Cliente no encontrado. ¿Desea crearlo? (s/n): ");
            if (crear) {
                boolean esEst = vista.confirmar("¿Es estudiante? (s/n): ");
                if (esEst) {
                    Estudiante e = vista.pedirDatosEstudianteConRut(rut);
                    if (e != null) {
                        tienda.agregarCliente(e);
                        cliente = e;
                    }
                } else {
                    Cliente c = vista.pedirDatosClienteConRut(rut);
                    if (c != null) {
                        tienda.agregarCliente(c);
                        cliente = c;
                    }
                }
            }
            if (cliente == null) {
                vista.mostrarError("No se puede continuar sin cliente.");
                return;
            }
        }
        ventaActual = new Venta(cliente);
        vista.mostrarMensaje("Venta iniciada para el cliente: " + cliente.getNombre());

        String idProducto;
        do {
            idProducto = vista.pedirIdProducto();
            if (!idProducto.equalsIgnoreCase("fin")) {
                int cantidad = vista.pedirCantidad();
                if (cantidad > 0) {
                    agregarProducto(idProducto, cantidad);
                } else {
                    vista.mostrarError("Cantidad debe ser mayor a cero.");
                }
            }
        } while (!idProducto.equalsIgnoreCase("fin"));

        finalizarVenta();
    }

    public void agregarProducto(String idProducto, int cantidad) {
        Producto producto = tienda.buscarProducto(idProducto);
        if (producto == null) {
            vista.mostrarError("Producto no encontrado.");
            return;
        }
        if (!producto.tieneStock(cantidad)) {
            vista.mostrarError("Stock insuficiente para: " + producto.getNombre());
            return;
        }
        ventaActual.agregarDetalle(producto, cantidad);
        vista.mostrarMensaje("Producto agregado: " + producto.getNombre() + " x" + cantidad);
    }

    public void finalizarVenta() {
        if (ventaActual != null && !ventaActual.getDetalles().isEmpty()) {
            Cliente c = ventaActual.getCliente();
            double total = ventaActual.calcularTotal();
            boolean pagado = true;
            if (c instanceof Estudiante) {
                int metodo = vista.seleccionarMetodoPago(); // 1 efectivo, 2 beca
                if (metodo == 2) {
                    Estudiante e = (Estudiante) c;
                    if (!e.pagarConBeca(total)) {
                        vista.mostrarError("Saldo de beca insuficiente. Se requiere pago en efectivo.");
                        pagado = vista.confirmar("¿Confirmar pago en efectivo? (s/n): ");
                    }
                }
            }
            if (pagado) {
                tienda.registrarVenta(ventaActual);
                vista.mostrarVenta(ventaActual);
            } else {
                vista.mostrarMensaje("Venta no pagada. Cancelada.");
            }
        } else {
            vista.mostrarMensaje("Venta cancelada o sin productos.");
        }
        ventaActual = null;
    }

    public static void main(String[] args) {
        Tienda tienda = new Tienda();
        VistaConsola vista = new VistaConsola();
        VentaController controlador = new VentaController(tienda, vista);
        controlador.iniciar();
    }
}
