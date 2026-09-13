package co.edu.uptc.modelo;

import java.time.LocalDate;
import co.edu.uptc.datos.Inventario;

// Representa la compra de un único libro.
// Al crearse, calcula automáticamente el impuesto y el total.

public class Compra {
    private static int contadorId = 1;

    private int idCompra;
    private LocalDate fechaCompra;
    private Libro libroComprado;
    private double impuestos;
    private double totalCompra;
 // Se incrementa cada vez que se crea una compra, para darle un id único
    public Compra(Libro libroComprado) {
        this.idCompra = contadorId++;
        this.fechaCompra = LocalDate.now();
        this.libroComprado = libroComprado;
        this.impuestos = libroComprado.getPrecio() * 0.19;
        this.totalCompra = libroComprado.getPrecio() + impuestos;
// Crea una compra para el libro dado y calcula impuestos (19%) y total
    }

    public String generarRecibo() {
        return "Recibo #" + idCompra +
                "\nFecha: " + fechaCompra +
                "\nLibro: " + libroComprado.getTitulo() +
                "\nImpuestos: $" + String.format("%.0f", impuestos) +
                "\nTotal: $" + String.format("%.0f", totalCompra);
// Genera el texto tipo recibo con los datos de las compras                
    }

    public void actualizarInventario(Inventario inventario) {
        int cantidadActual = libroComprado.getCantidadDisponible();
        if (cantidadActual > 0) {
            libroComprado.setCantidadDisponible(cantidadActual - 1);
            inventario.actualizarLibro(libroComprado);
            
// Descuenta una unidad del libro comprado y sincroniza el cambio
// con el inventario. Solo descuenta si todavía hay unidades disponibles.
        }
    }

    public int getIdCompra() { return idCompra; }
    public double getImpuestos() { return impuestos; }
    public double getTotalCompra() { return totalCompra; }
}
