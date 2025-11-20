package Modelo;

import java.util.List;

public interface ClientesRepositorio {
    List<Cliente> cargarClientes();
    void guardarClientes(List<Cliente> clientes);
}
