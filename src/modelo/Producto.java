package modelo;

import java.io.Serializable;

public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private int precio;
    private int stock;

    public Producto(String id, String nombre, int precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getPrecio() { return precio; }
    public int getStock() { return stock; }

    public int calcularPrecio(int cantidad) {
        return this.precio * cantidad;
    }

    public void disminuirStock(int cantidad) {
        this.stock -= cantidad;
    }

    public void agregarStock(int cantidad) {
        this.stock += cantidad;
    }

    @Override
    public String toString() {
        return nombre + " ($" + precio + ") - Stock: " + stock;
    }
}