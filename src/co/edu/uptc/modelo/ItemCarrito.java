package co.edu.uptc.modelo;

public class ItemCarrito {
    private Libro libro;
    private int cantidad;

    public ItemCarrito(Libro libro, int cantidad) {
        this.libro = libro;
        this.cantidad = cantidad;
    }

    // Precio del libro multiplicado por la cantidad, sin impuestos
    public double calcularSubtotal() {
        return libro.getPrecio() * cantidad;
    }

    // El IVA depende del formato: 19% si es físico, 5% si es digital
    public double calcularIva() {
        double tasa = "Físico".equalsIgnoreCase(libro.getFormatoLibro()) ? 0.19 : 0.05;
        return calcularSubtotal() * tasa;
    }

    public Libro getLibro() { return libro; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}