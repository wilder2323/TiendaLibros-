package co.edu.uptc.modelo;

public class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private int anioPublicacion;
    private String categoria;
    private String editorial;
    private int numeroPaginas;
    private double precio;
    private int cantidadDisponible;
    private String formatoLibro;
// Representa un libro del catálogo de la tienda. 
// Es una clase de modelo: solo guarda datos y sabe describirse a sí misma,
// no contiene lógica de interfaz ni de inventario.

    public Libro(String isbn, String titulo, String autor, int anioPublicacion, String categoria,
                 String editorial, int numeroPaginas, double precio, int cantidadDisponible, String formatoLibro) {
// Construye un libro con todos sus datos iniciales  

// Arma el texto que se muestra en el panel "Información del Libro".
// Calcula si está disponible según la cantidad restante.
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.anioPublicacion = anioPublicacion;
        this.categoria = categoria;
        this.editorial = editorial;
        this.numeroPaginas = numeroPaginas;
        this.precio = precio;
        this.cantidadDisponible = cantidadDisponible;
        this.formatoLibro = formatoLibro;
    }

    public String obtenerDetalles() {
        return "ISBN: " + isbn +
                "\nTítulo: " + titulo +
                "\nAutor(es): " + autor +
                "\nGénero: " + categoria +
                "\nEditorial: " + editorial +
                "\nN° Páginas: " + numeroPaginas +
                "\nPrecio de venta: $" + String.format("%.0f", precio) +
                "\nCantidad Disponible: " + cantidadDisponible +
                "\nDisponible: " + (cantidadDisponible > 0 ? "Sí" : "No") +
                "\nFormato: " + formatoLibro;
    }
 // Getters y setters: permiten leer y modificar cada atributo desde otras clases
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public int getNumeroPaginas() { return numeroPaginas; }
    public void setNumeroPaginas(int numeroPaginas) { this.numeroPaginas = numeroPaginas; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(int cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }

    public String getFormatoLibro() { return formatoLibro; }
    public void setFormatoLibro(String formatoLibro) { this.formatoLibro = formatoLibro; }

    @Override
    public String toString() { return titulo; }
}
