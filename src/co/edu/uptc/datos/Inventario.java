package co.edu.uptc.datos;

import co.edu.uptc.modelo.Libro;
import java.util.ArrayList;
import java.util.List;

// Guarda y administra la lista de libros de la tienda en memoria.
// Es la capa de datos: la interfaz gráfica nunca modifica la lista
// directamente, siempre pasa por esta clase.
public class Inventario {
    private List<Libro> listaLibros = new ArrayList<>();

    public List<Libro> listaLibros() {
        return listaLibros;
    }

    // Devuelve solo los libros con unidades en stock, para que el cliente
    // no vea ni pueda comprar libros agotados en el catálogo
    public List<Libro> listarDisponibles() {
        List<Libro> disponibles = new ArrayList<>();
        for (Libro l : listaLibros) {
            if (l.getCantidadDisponible() > 0) disponibles.add(l);
        }
        return disponibles;
    }

    // Busca un libro por su ISBN; se usa para validar que no se dupliquen
    // ISBN al añadir uno nuevo
    public Libro buscarPorIsbn(String isbn) {
        for (Libro l : listaLibros) {
            if (l.getIsbn().equals(isbn)) return l;
        }
        return null;
    }

    // Agrega un libro nuevo al inventario
    public void agregarLibro(Libro libro) {
        listaLibros.add(libro);
    }

    // Reemplaza un libro existente por su versión actualizada.
    // Se busca por posición en la lista, ya que es el mismo objeto en memoria.
    public void actualizarLibro(Libro libro) {
        int indice = listaLibros.indexOf(libro);
        if (indice != -1) {
            listaLibros.set(indice, libro);
        }
    }

    // Elimina un libro del inventario según su ISBN
    public void eliminarLibro(String isbn) {
        listaLibros.removeIf(l -> l.getIsbn().equals(isbn));
    }

    // Indica si un libro tiene unidades disponibles
    public boolean verificarDisponibilidad(String isbn) {
        for (Libro l : listaLibros) {
            if (l.getIsbn().equals(isbn)) {
                return l.getCantidadDisponible() > 0;
            }
        }
        return false;
    }
}