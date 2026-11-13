package co.edu.uptc.gui;

import javax.swing.*;
import java.awt.*;
import co.edu.uptc.modelo.Libro;
import co.edu.uptc.datos.Inventario;

// Pantalla principal después del login.
// Muestra solo los libros disponibles, permite agregarlos al carrito,
// y conserva los botones de administración (Añadir, Eliminar, Actualizar).
public class PanelCatalogo extends JPanel {

    private VentanaPrincipal ventana;
    private Inventario inventario;

    private DefaultListModel<Libro> modeloLista = new DefaultListModel<>();
    private JList<Libro> tablaLibros = new JList<>(modeloLista);
    private JTextArea areaInfo = new JTextArea();
    private JTextField campoCantidad = new JTextField(4);
    private JButton botonAgregarCarrito = new JButton("Agregar al Carrito");

    public PanelCatalogo(VentanaPrincipal ventana, Inventario inventario) {
        this.ventana = ventana;
        this.inventario = inventario;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.add(new JLabel("Tienda Virtual de Libros"), BorderLayout.WEST);

        JPanel botonesEncabezado = new JPanel();
        JButton botonCarrito = new JButton("Carrito");
        botonCarrito.addActionListener(e -> ventana.mostrarPanelCarrito());
        JButton botonPerfil = new JButton("Perfil");
        botonPerfil.addActionListener(e -> ventana.mostrarPanelPerfil());
        botonesEncabezado.add(botonCarrito);
        botonesEncabezado.add(botonPerfil);
        encabezado.add(botonesEncabezado, BorderLayout.EAST);
        add(encabezado, BorderLayout.NORTH);

        JPanel panelLista = new JPanel(new BorderLayout());
        panelLista.setBorder(BorderFactory.createTitledBorder("Listado de Libros Disponibles"));
        tablaLibros.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarInfo();
        });
        panelLista.add(new JScrollPane(tablaLibros), BorderLayout.CENTER);
        panelLista.setPreferredSize(new Dimension(200, 0));

        JPanel panelInfo = new JPanel(new BorderLayout(0, 8));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información del Libro"));
        areaInfo.setEditable(false);
        areaInfo.setText("Selecciona un libro de la lista.");
        panelInfo.add(new JScrollPane(areaInfo), BorderLayout.CENTER);

        JPanel panelCompra = new JPanel();
        panelCompra.add(new JLabel("Cantidad:"));
        panelCompra.add(campoCantidad);
        botonAgregarCarrito.setEnabled(false);
        botonAgregarCarrito.addActionListener(e -> agregarAlCarrito());
        panelCompra.add(botonAgregarCarrito);

        JPanel acciones = new JPanel(new GridLayout(1, 3, 6, 0));
        JButton botonAñadir = new JButton("Añadir");
        JButton botonEliminar = new JButton("Eliminar");
        JButton botonActualizar = new JButton("Actualizar");
        botonAñadir.addActionListener(e -> abrirFormulario(false));
        botonActualizar.addActionListener(e -> abrirFormulario(true));
        botonEliminar.addActionListener(e -> eliminarLibro());
        acciones.add(botonAñadir);
        acciones.add(botonEliminar);
        acciones.add(botonActualizar);

        JPanel panelSur = new JPanel(new GridLayout(2, 1));
        panelSur.add(panelCompra);
        panelSur.add(acciones);
        panelInfo.add(panelSur, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout(10, 0));
        centro.add(panelLista, BorderLayout.WEST);
        centro.add(panelInfo, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    // Vuelve a leer del inventario solo los libros disponibles y los muestra en la lista
    public void cargarLibros() {
        modeloLista.clear();
        for (Libro l : inventario.listarDisponibles()) modeloLista.addElement(l);
        mostrarInfo();
    }

    private Libro libroSeleccionado() {
        return tablaLibros.getSelectedValue();
    }

    // Muestra los detalles del libro seleccionado y habilita "Agregar al Carrito"
    // solo si todavía quedan unidades disponibles
    private void mostrarInfo() {
        Libro l = libroSeleccionado();
        if (l == null) {
            areaInfo.setText("Selecciona un libro de la lista.");
            botonAgregarCarrito.setEnabled(false);
        } else {
            areaInfo.setText(l.obtenerDetalles());
            botonAgregarCarrito.setEnabled(l.getCantidadDisponible() > 0);
        }
    }

    // Valida la cantidad ingresada (numérica, mínimo 1, no mayor al stock)
    // y, si todo está bien, agrega el libro al carrito compartido de la ventana
    private void agregarAlCarrito() {
        Libro l = libroSeleccionado();
        if (l == null) return;
        try {
            int cantidad = Integer.parseInt(campoCantidad.getText().trim());
            if (cantidad < 1) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mínimo 1.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cantidad > l.getCantidadDisponible()) {
                JOptionPane.showMessageDialog(this, "La cantidad no puede superar el inventario disponible.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ventana.getCarrito().agregarLibro(l, cantidad);
            campoCantidad.setText("");
            JOptionPane.showMessageDialog(this, "Libro agregado al carrito.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa una cantidad numérica válida.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Formulario reutilizado tanto para Añadir como para Actualizar.
    // Si es actualización, precarga los datos y bloquea el ISBN (no se puede cambiar).
    private void abrirFormulario(boolean actualizar) {
        Libro existente = actualizar ? libroSeleccionado() : null;
        if (actualizar && existente == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro para actualizar.");
            return;
        }

        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                actualizar ? "Actualizar libro" : "Añadir libro", true);
        dialogo.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;

        JTextField campoIsbn = new JTextField(15);
        JTextField campoTitulo = new JTextField(15);
        JTextField campoAutor = new JTextField(15);
        JTextField campoGenero = new JTextField(15);
        JTextField campoEditorial = new JTextField(15);
        JTextField campoAnio = new JTextField(15);
        JTextField campoPaginas = new JTextField(15);
        JTextField campoPrecio = new JTextField(15);
        JTextField campoCantidadLibro = new JTextField(15);
        JComboBox<String> campoFormato = new JComboBox<>(new String[]{"Físico", "Digital"});

        String[] etiquetas = {"ISBN", "Título", "Autor", "Género", "Editorial",
                "Año de publicación", "N° de Páginas", "Precio", "Cantidad", "Formato"};
        JComponent[] campos = {campoIsbn, campoTitulo, campoAutor, campoGenero, campoEditorial,
                campoAnio, campoPaginas, campoPrecio, campoCantidadLibro, campoFormato};

        for (int i = 0; i < etiquetas.length; i++) {
            c.gridy = i * 2;
            dialogo.add(new JLabel(etiquetas[i]), c);
            c.gridy = i * 2 + 1;
            dialogo.add(campos[i], c);
        }

        if (actualizar) {
            campoIsbn.setText(existente.getIsbn());
            campoIsbn.setEnabled(false); // el ISBN no se puede modificar en una actualización
            campoTitulo.setText(existente.getTitulo());
            campoAutor.setText(existente.getAutor());
            campoGenero.setText(existente.getCategoria());
            campoEditorial.setText(existente.getEditorial());
            campoAnio.setText(String.valueOf(existente.getAnioPublicacion()));
            campoPaginas.setText(String.valueOf(existente.getNumeroPaginas()));
            campoPrecio.setText(String.valueOf(existente.getPrecio()));
            campoCantidadLibro.setText(String.valueOf(existente.getCantidadDisponible()));
            campoFormato.setSelectedItem(existente.getFormatoLibro());
        }

        JLabel etiquetaError = new JLabel(" ");
        etiquetaError.setForeground(Color.RED);
        c.gridy = etiquetas.length * 2;
        dialogo.add(etiquetaError, c);

        // Valida ISBN (obligatorio y único), título, autor, año (1900 a hoy),
        // precio (mayor que 0) y cantidad (no negativa) antes de guardar
        JButton botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(e -> {
            String isbn = campoIsbn.getText().trim();
            String titulo = campoTitulo.getText().trim();
            String autor = campoAutor.getText().trim();

            if (isbn.isEmpty()) { etiquetaError.setText("El ISBN es obligatorio."); return; }
            if (!actualizar && inventario.buscarPorIsbn(isbn) != null) {
                etiquetaError.setText("Ya existe un libro con ese ISBN.");
                return;
            }
            if (titulo.isEmpty()) { etiquetaError.setText("El título es obligatorio."); return; }
            if (autor.isEmpty()) { etiquetaError.setText("El autor es obligatorio."); return; }

            try {
                int anio = Integer.parseInt(campoAnio.getText().trim());
                int anioActual = java.time.Year.now().getValue();
                if (anio < 1900 || anio > anioActual) {
                    etiquetaError.setText("El año debe estar entre 1900 y " + anioActual + ".");
                    return;
                }
                int paginas = Integer.parseInt(campoPaginas.getText().trim());
                double precio = Double.parseDouble(campoPrecio.getText().trim());
                if (precio <= 0) { etiquetaError.setText("El precio debe ser mayor que 0."); return; }
                int cantidad = Integer.parseInt(campoCantidadLibro.getText().trim());
                if (cantidad < 0) { etiquetaError.setText("La cantidad no puede ser negativa."); return; }

                if (actualizar) {
                    existente.setTitulo(titulo);
                    existente.setAutor(autor);
                    existente.setCategoria(campoGenero.getText().trim());
                    existente.setEditorial(campoEditorial.getText().trim());
                    existente.setAnioPublicacion(anio);
                    existente.setNumeroPaginas(paginas);
                    existente.setPrecio(precio);
                    existente.setCantidadDisponible(cantidad);
                    existente.setFormatoLibro((String) campoFormato.getSelectedItem());
                    inventario.actualizarLibro(existente);
                } else {
                    Libro nuevo = new Libro(isbn, titulo, autor, anio, campoGenero.getText().trim(),
                            campoEditorial.getText().trim(), paginas, precio, cantidad,
                            (String) campoFormato.getSelectedItem());
                    inventario.agregarLibro(nuevo);
                }
                cargarLibros();
                dialogo.dispose();
            } catch (NumberFormatException ex) {
                etiquetaError.setText("Año, N° de Páginas, Precio y Cantidad deben ser numéricos.");
            }
        });
        c.gridy = etiquetas.length * 2 + 1;
        dialogo.add(botonGuardar, c);

        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    // No permite eliminar un libro si ya tiene ventas registradas,
    // según la regla del documento
    private void eliminarLibro() {
        Libro libro = libroSeleccionado();
        if (libro == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro para eliminar.");
            return;
        }
        if (ventana.getRepositorioCompras().tieneVentasDe(libro.getIsbn())) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar: este libro tiene ventas asociadas.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar \"" + libro.getTitulo() + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            inventario.eliminarLibro(libro.getIsbn());
            cargarLibros();
        }
    }
}