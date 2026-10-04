package co.edu.uptc.modelo;

import java.util.ArrayList;
import java.util.List;

// Representa el carrito de compras de un cliente mientras navega la tienda.
// Guarda una lista de ItemCarrito (libro + cantidad) y sabe calcular sus propios totales.
public class Carrito {
    private List<ItemCarrito> items = new ArrayList<>();

    public List<ItemCarrito> getItems() { return items; }

    // Si el libro ya está en el carrito, solo suma la cantidad;
    // si no está, agrega un ítem nuevo
    public void agregarLibro(Libro libro, int cantidad) {
        for (ItemCarrito item : items) {
            if (item.getLibro().getIsbn().equals(libro.getIsbn())) {
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        items.add(new ItemCarrito(libro, cantidad));
    }

    // Cambia la cantidad de un libro que ya está en el carrito
    public void actualizarCantidad(String isbn, int nuevaCantidad) {
        for (ItemCarrito item : items) {
            if (item.getLibro().getIsbn().equals(isbn)) {
                item.setCantidad(nuevaCantidad);
                return;
            }
        }
    }

    // Quita del carrito el libro con ese ISBN
    public void eliminarLibro(String isbn) {
        items.removeIf(item -> item.getLibro().getIsbn().equals(isbn));
    }

    // Suma el subtotal de todos los ítems (sin impuestos ni descuento)
    public double calcularSubtotal() {
        double total = 0;
        for (ItemCarrito item : items) total += item.calcularSubtotal();
        return total;
    }

    // Suma el IVA de todos los ítems (cada uno con su propia tasa según formato)
    public double calcularTotalImpuestos() {
        double total = 0;
        for (ItemCarrito item : items) total += item.calcularIva();
        return total;
    }

    // Solo los clientes Premium reciben el 10% de descuento sobre el subtotal
    public double calcularDescuento(Cliente cliente) {
        if (cliente != null && cliente.esPremium()) {
            return calcularSubtotal() * 0.10;
        }
        return 0;
    }

    // Total final: subtotal + impuestos - descuento (si aplica)
    public double calcularTotal(Cliente cliente) {
        return calcularSubtotal() + calcularTotalImpuestos() - calcularDescuento(cliente);
    }

    public boolean estaVacio() { return items.isEmpty(); }

    // Se usa después de finalizar una compra, para dejar el carrito listo para la siguiente
    public void vaciar() { items.clear(); }
}