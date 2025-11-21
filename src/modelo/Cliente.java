package modelo;

import java.io.Serializable;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rut;
    private String nombre;
    private Integer saldoJunaeb;

    public Cliente(String rut, String nombre) {
        this.rut = rut;
        this.nombre = nombre;
        this.saldoJunaeb = null;
    }

    public String getRut() { return rut; }
    public String getNombre() { return nombre; }

    public Integer getSaldoJunaeb() { return saldoJunaeb; }
    public void setSaldoJunaeb(Integer saldo) { this.saldoJunaeb = saldo; }

    public boolean esEstudianteJunaeb() {
        return saldoJunaeb != null;
    }
}