package co.edu.uptc.datos;

import co.edu.uptc.modelo.Cliente;
import java.util.ArrayList;
import java.util.List;

// Guarda a todos los clientes que se han registrado en la tienda.
// Es quien responde si un correo ya existe y quien valida el login.
public class RepositorioClientes {
    private List<Cliente> clientes = new ArrayList<>();

    // Se usa al registrarse, para no permitir correos repetidos
    public boolean correoRegistrado(String correo) {
        for (Cliente c : clientes) {
            if (c.getCorreo().equalsIgnoreCase(correo)) return true;
        }
        return false;
    }

    public void registrarCliente(Cliente cliente) {
        clientes.add(cliente);
    }

    // Busca un cliente por su correo; devuelve null si no existe
    public Cliente buscarPorCorreo(String correo) {
        for (Cliente c : clientes) {
            if (c.getCorreo().equalsIgnoreCase(correo)) return c;
        }
        return null;
    }

    // Valida correo + contraseña para el login.
    // Si el correo no existe o la contraseña no coincide, devuelve null.
    public Cliente autenticar(String correo, String contraseña) {
        Cliente cliente = buscarPorCorreo(correo);
        if (cliente != null && cliente.verificarContraseña(contraseña)) {
            return cliente;
        }
        return null;
    }
}