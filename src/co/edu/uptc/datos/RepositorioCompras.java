package co.edu.uptc.datos;

import co.edu.uptc.modelo.Cliente;
import co.edu.uptc.modelo.Compra;
import co.edu.uptc.modelo.ItemCarrito;
import java.util.ArrayList;
import java.util.List;

// Guarda todas las compras ya finalizadas de todos los clientes.
// Sirve tanto para el historial de compras como para saber si un libro
// tiene ventas asociadas (y por lo tanto no se puede eliminar del catálogo).
public class RepositorioCompras {
    private List<Compra> compras = new ArrayList<>();

    public void registrarCompra(Compra compra) {
        compras.add(compra);
    }

    // Filtra solo las compras que pertenecen a ese cliente en particular
    public List<Compra> historialDeCliente(Cliente cliente) {
        List<Compra> resultado = new ArrayList<>();
        for (Compra c : compras) {
            if (c.getCliente() == cliente) resultado.add(c);
        }
        return resultado;
    }

    // Revisa si algún libro comprado alguna vez tiene ese ISBN.
    // Se usa para bloquear la eliminación de libros con ventas asociadas.
    public boolean tieneVentasDe(String isbn) {
        for (Compra c : compras) {
            for (ItemCarrito item : c.getLibrosComprados()) {
                if (item.getLibro().getIsbn().equals(isbn)) return true;
            }
        }
        return false;
    }
}