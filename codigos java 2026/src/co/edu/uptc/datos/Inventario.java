package co.edu.uptc.datos;

import co.edu.uptc.modelo.Libro;
import java.util.ArrayList;
import java.util.List;
// Guarda y administra la lista de libros de la tienda en memoria.
// Es la capa de datos: la interfaz gráfica nunca modifica la lista
// directamente, siempre pasa por esta clase.
public class Inventario {
    private List<Libro> listaLibros = new ArrayList<>();
// Lista donde se guardan todos los libros mientras el programa está abierto
    public Inventario() {
        cargarLibrosIniciales();
    }

    private void cargarLibrosIniciales() {
        
    }

    public List<Libro> listaLibros() {
        return listaLibros;
    }

    public void agregarLibro(Libro libro) { // Agrega un libro nuevo al inventario
        listaLibros.add(libro);
    }

    public void actualizarLibro(Libro libro) {
        int indice = listaLibros.indexOf(libro);
        if (indice != -1) {
            listaLibros.set(indice, libro);
        }
    // Reemplaza un libro existente por su versión actualizada.
    // Se busca por posición en la lista, ya que es el mismo objeto en memoria.     
    }

    public void eliminarLibro(String isbn) {
        listaLibros.removeIf(l -> l.getIsbn().equals(isbn)); // Elimina un libro del inventario según su ISBN
    }

    public boolean verificarDisponibilidad(String isbn) {
        for (Libro l : listaLibros) {
            if (l.getIsbn().equals(isbn)) {
                return l.getCantidadDisponible() > 0; // Indica si un libro tiene unidades disponibles
            }
        }
        return false;
    }
}
