package co.edu.uptc.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import co.edu.uptc.datos.Inventario;

// Representa una compra ya finalizada, con todos los libros del carrito
// en el momento de confirmar, el método de pago y los totales calculados.
public class Compra {
    private static int contadorId = 1; // se incrementa en cada compra, para darle un id único

    private int idCompra;
    private Cliente cliente;
    private LocalDate fechaCompra;
    private List<ItemCarrito> librosComprados;
    private double subtotal;
    private double totalImpuestos;
    private double descuento;
    private double totalCompra;
    private String metodoPago;

    // Al crearse, copia los libros del carrito y calcula subtotal, IVA,
    // descuento Premium (si aplica) y el total final
    public Compra(Cliente cliente, Carrito carrito, String metodoPago) {
        this.idCompra = contadorId++;
        this.cliente = cliente;
        this.fechaCompra = LocalDate.now();
        this.librosComprados = new ArrayList<>(carrito.getItems()); // copia, no referencia directa al carrito
        this.subtotal = carrito.calcularSubtotal();
        this.totalImpuestos = carrito.calcularTotalImpuestos();
        this.descuento = carrito.calcularDescuento(cliente);
        this.totalCompra = carrito.calcularTotal(cliente);
        this.metodoPago = metodoPago;
    }

    // Arma el texto del recibo con el detalle de libros, subtotal, IVA,
    // descuento (si hubo) y total a pagar
    public String generarRecibo() {
        StringBuilder recibo = new StringBuilder();
        recibo.append("Recibo #").append(idCompra).append("\n");
        recibo.append("Fecha: ").append(fechaCompra).append("\n");
        recibo.append("Cliente: ").append(cliente.getNombreCompleto())
              .append(" (").append(cliente.getTipoCliente()).append(")\n\n");

        for (ItemCarrito item : librosComprados) {
            recibo.append(item.getCantidad()).append(" x ").append(item.getLibro().getTitulo())
                  .append(" = $").append(String.format("%.0f", item.calcularSubtotal())).append("\n");
        }

        recibo.append("\nSubtotal: $").append(String.format("%.0f", subtotal)).append("\n");
        recibo.append("IVA: $").append(String.format("%.0f", totalImpuestos)).append("\n");
        if (descuento > 0) {
            recibo.append("Descuento Premium: -$").append(String.format("%.0f", descuento)).append("\n");
        }
        recibo.append("Total a pagar: $").append(String.format("%.0f", totalCompra)).append("\n");
        recibo.append("Método de pago: ").append(metodoPago);
        return recibo.toString();
    }

    // Descuenta del inventario la cantidad comprada de cada libro.
    // Nunca deja el stock en negativo (se protege con Math.max).
    public void actualizarInventario(Inventario inventario) {
        for (ItemCarrito item : librosComprados) {
            Libro libro = item.getLibro();
            int nuevaCantidad = Math.max(libro.getCantidadDisponible() - item.getCantidad(), 0);
            libro.setCantidadDisponible(nuevaCantidad);
            inventario.actualizarLibro(libro);
        }
    }

    public int getIdCompra() { return idCompra; }
    public Cliente getCliente() { return cliente; }
    public LocalDate getFechaCompra() { return fechaCompra; }
    public List<ItemCarrito> getLibrosComprados() { return librosComprados; }
    public double getTotalCompra() { return totalCompra; }
    public String getMetodoPago() { return metodoPago; }
}