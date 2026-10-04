package co.edu.uptc.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import co.edu.uptc.modelo.Cliente;
import co.edu.uptc.modelo.Carrito;
import co.edu.uptc.modelo.Compra;
import co.edu.uptc.modelo.ItemCarrito;
import co.edu.uptc.datos.Inventario;
import co.edu.uptc.datos.RepositorioCompras;

// Pantalla del carrito: muestra los libros agregados, los totales
// (subtotal, IVA, descuento, total) y permite finalizar la compra.
public class PanelCarrito extends JPanel {

    private VentanaPrincipal ventana;
    private Inventario inventario;
    private RepositorioCompras repositorioCompras;

    private DefaultTableModel modeloTabla;
    private JTable tablaCarrito;
    private JLabel etiquetaSubtotal = new JLabel();
    private JLabel etiquetaImpuestos = new JLabel();
    private JLabel etiquetaDescuento = new JLabel();
    private JLabel etiquetaTotal = new JLabel();
    private JComboBox<String> comboMetodoPago = new JComboBox<>(new String[]{"Tarjeta", "Efectivo", "Transferencia"});

    public PanelCarrito(VentanaPrincipal ventana, Inventario inventario, RepositorioCompras repositorioCompras) {
        this.ventana = ventana;
        this.inventario = inventario;
        this.repositorioCompras = repositorioCompras;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.add(new JLabel("Carrito de Compras"), BorderLayout.WEST);
        JButton botonVolver = new JButton("Volver al Catálogo");
        botonVolver.addActionListener(e -> ventana.mostrarPanelCatalogo());
        encabezado.add(botonVolver, BorderLayout.EAST);
        add(encabezado, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"Libro", "Cantidad", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; } // tabla de solo lectura
        };
        tablaCarrito = new JTable(modeloTabla);
        add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0; panelInferior.add(new JLabel("Subtotal:"), c);
        c.gridx = 1; panelInferior.add(etiquetaSubtotal, c);
        c.gridx = 0; c.gridy = 1; panelInferior.add(new JLabel("IVA:"), c);
        c.gridx = 1; panelInferior.add(etiquetaImpuestos, c);
        c.gridx = 0; c.gridy = 2; panelInferior.add(new JLabel("Descuento Premium:"), c);
        c.gridx = 1; panelInferior.add(etiquetaDescuento, c);
        c.gridx = 0; c.gridy = 3; panelInferior.add(new JLabel("Total a pagar:"), c);
        c.gridx = 1; panelInferior.add(etiquetaTotal, c);
        c.gridx = 0; c.gridy = 4; panelInferior.add(new JLabel("Método de pago:"), c);
        c.gridx = 1; panelInferior.add(comboMetodoPago, c);

        JPanel botones = new JPanel();
        JButton botonEliminar = new JButton("Eliminar Seleccionado");
        botonEliminar.addActionListener(e -> eliminarSeleccionado());
        JButton botonFinalizar = new JButton("Finalizar Compra");
        botonFinalizar.addActionListener(e -> finalizarCompra());
        botones.add(botonEliminar);
        botones.add(botonFinalizar);
        c.gridx = 0; c.gridy = 5; c.gridwidth = 2;
        panelInferior.add(botones, c);

        add(panelInferior, BorderLayout.SOUTH);
    }

    // Repinta la tabla y recalcula los totales a partir del carrito compartido.
    // Se llama cada vez que el carrito cambia (agregar, eliminar, finalizar).
    public void actualizarVista(Cliente cliente) {
        modeloTabla.setRowCount(0);
        Carrito carrito = ventana.getCarrito();
        for (ItemCarrito item : carrito.getItems()) {
            modeloTabla.addRow(new Object[]{
                    item.getLibro().getTitulo(),
                    item.getCantidad(),
                    String.format("$%.0f", item.calcularSubtotal())
            });
        }
        etiquetaSubtotal.setText(String.format("$%.0f", carrito.calcularSubtotal()));
        etiquetaImpuestos.setText(String.format("$%.0f", carrito.calcularTotalImpuestos()));
        etiquetaDescuento.setText(String.format("$%.0f", carrito.calcularDescuento(cliente)));
        etiquetaTotal.setText(String.format("$%.0f", carrito.calcularTotal(cliente)));
    }

    private void eliminarSeleccionado() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro del carrito para eliminar.");
            return;
        }
        ItemCarrito item = ventana.getCarrito().getItems().get(fila);
        ventana.getCarrito().eliminarLibro(item.getLibro().getIsbn());
        actualizarVista(ventana.getClienteActual());
    }

    // Revalida que haya stock suficiente (pudo cambiar desde que se agregó al carrito),
    // crea la Compra, descuenta el inventario, la guarda en el repositorio
    // y muestra el recibo generado
    private void finalizarCompra() {
        Carrito carrito = ventana.getCarrito();
        if (carrito.estaVacio()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (ItemCarrito item : carrito.getItems()) {
            if (item.getCantidad() > item.getLibro().getCantidadDisponible()) {
                JOptionPane.showMessageDialog(this,
                        "\"" + item.getLibro().getTitulo() + "\" ya no tiene suficiente inventario disponible.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String metodoPago = (String) comboMetodoPago.getSelectedItem();
        Cliente cliente = ventana.getClienteActual();

        Compra compra = new Compra(cliente, carrito, metodoPago);
        compra.actualizarInventario(inventario);
        repositorioCompras.registrarCompra(compra);

        JTextArea areaRecibo = new JTextArea(compra.generarRecibo());
        areaRecibo.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(areaRecibo), "Compra Finalizada", JOptionPane.INFORMATION_MESSAGE);

        carrito.vaciar(); // el carrito queda listo para una próxima compra
        actualizarVista(cliente);
    }
}