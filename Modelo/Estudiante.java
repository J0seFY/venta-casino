package Modelo;

public class Estudiante extends Cliente {
    private double saldoBeca;

    public Estudiante(String rut, String nombre, double saldoBeca) {
        super(rut, nombre);
        this.saldoBeca = saldoBeca;
    }

    public boolean pagarConBeca(double monto) {
        if (this.saldoBeca >= monto) {
            this.saldoBeca -= monto;
            return true;
        }
        return false;
    }

    public double getSaldoBeca() {
        return saldoBeca;
    }
}
