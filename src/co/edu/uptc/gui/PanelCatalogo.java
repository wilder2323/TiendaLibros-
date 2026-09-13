package co.edu.uptc.gui;

import javax.swing.*;
import java.awt.*;
import co.edu.uptc.modelo.Cliente;
import co.edu.uptc.modelo.Compra;
import co.edu.uptc.modelo.Libro;
import co.edu.uptc.datos.Inventario;
// Pantalla principal después del login.
// Muestra el listado de libros, la información del seleccionado
// y los botones para comprar, añadir, eliminar y actualizar.
public class PanelCatalogo extends JPanel {

    private VentanaPrincipal ventana;
    private Inventario inventario;

    private DefaultListModel<Libro> modeloLista = new DefaultListModel<>();
    private JList<Libro> tablaLibros = new JList<>(modeloLista);
    private JTextArea areaInfo = new JTextArea();
    private JButton botonComprar = new JButton("Comprar");

    public PanelCatalogo(VentanaPrincipal ventana, Inventario inventario) {
        this.ventana = ventana;
        this.inventario = inventario;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.add(new JLabel("Tienda de Libros"), BorderLayout.WEST);
        JButton botonPerfil = new JButton("Perfil");
        botonPerfil.addActionListener(e -> mostrarPerfil());
        encabezado.add(botonPerfil, BorderLayout.EAST);
        add(encabezado, BorderLayout.NORTH);

        JPanel panelLista = new JPanel(new BorderLayout());
        panelLista.setBorder(BorderFactory.createTitledBorder("Listado de Libros"));
        tablaLibros.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarInfo();
        });
        panelLista.add(new JScrollPane(tablaLibros), BorderLayout.CENTER);
        panelLista.setPreferredSize(new Dimension(180, 0));

        JPanel panelInfo = new JPanel(new BorderLayout(0, 8));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información del Libro"));
        areaInfo.setEditable(false);
        areaInfo.setText("Selecciona un libro de la lista.");
        panelInfo.add(new JScrollPane(areaInfo), BorderLayout.CENTER);

        botonComprar.setEnabled(false);
        botonComprar.addActionListener(e -> comprarLibro());

        JPanel acciones = new JPanel(new GridLayout(1, 4, 6, 0));
        JButton botonAñadir = new JButton("Añadir");
        JButton botonEliminar = new JButton("Eliminar");
        JButton botonActualizar = new JButton("Actualizar");
        botonAñadir.addActionListener(e -> abrirFormulario(false));
        botonActualizar.addActionListener(e -> abrirFormulario(true));
        botonEliminar.addActionListener(e -> eliminarLibro());
        acciones.add(botonComprar);
        acciones.add(botonAñadir);
        acciones.add(botonEliminar);
        acciones.add(botonActualizar);
        panelInfo.add(acciones, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout(10, 0));
        centro.add(panelLista, BorderLayout.WEST);
        centro.add(panelInfo, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }
    // Vuelve a leer los libros del inventario y los muestra en la lista.
    // Se llama cada vez que hay un cambio (compra, añadir, eliminar, actualizar).
    public void cargarLibros() {
        modeloLista.clear();
        for (Libro l : inventario.listaLibros()) modeloLista.addElement(l);
        mostrarInfo();
    }

    private Libro libroSeleccionado() {
        return tablaLibros.getSelectedValue();
    }
    // Muestra los detalles del libro seleccionado en el panel de información.
    // Si no hay unidades disponibles, deshabilita el botón "Comprar".
    private void mostrarInfo() {
        Libro l = libroSeleccionado();
        if (l == null) {
            areaInfo.setText("Selecciona un libro de la lista.");
            botonComprar.setEnabled(false);
        } else {
            areaInfo.setText(l.obtenerDetalles());
            botonComprar.setEnabled(true);
        }
    }
    
    // Muestra el diálogo de perfil con el nombre del cliente logueado
    // y permite cerrar sesión, volviendo a la pantalla de login.
    private void mostrarPerfil() {
        Cliente cliente = ventana.getClienteActual();
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Perfil", true);
        dialogo.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.gridx = 0; c.gridy = 0;
        dialogo.add(new JLabel("Nombre Completo: " + cliente.getNombreCompleto()), c);

        JButton botonCerrarSesion = new JButton("Cerrar sesión");
        botonCerrarSesion.addActionListener(e -> {
            dialogo.dispose();
            tablaLibros.clearSelection();
            ventana.setClienteActual(null);
            ventana.mostrarPanelInicioSesion();
        });
        c.gridy = 1;
        dialogo.add(botonCerrarSesion, c);

        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
    // Abre el diálogo de compra: crea una Compra (que calcula impuestos
    // y total) y, al confirmar, descuenta la unidad del inventario.
    private void comprarLibro() {
        Libro libro = libroSeleccionado();
        if (libro == null) return;

        Compra compra = new Compra(libro);

        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Comprar libro", true);
        dialogo.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.gridx = 0; c.anchor = GridBagConstraints.WEST;

        c.gridy = 0; dialogo.add(new JLabel("Nombre del Libro: " + libro.getTitulo()), c);
        c.gridy = 1; dialogo.add(new JLabel(String.format("Impuestos: $%.0f", compra.getImpuestos())), c);
        c.gridy = 2; dialogo.add(new JLabel(String.format("Total: $%.0f", compra.getTotalCompra())), c);

        JButton botonConfirmar = new JButton("Confirmar");
        botonConfirmar.addActionListener(e -> {
            compra.actualizarInventario(inventario);
            mostrarInfo();
            dialogo.dispose();
        });
        c.gridy = 3; dialogo.add(botonConfirmar, c);

        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
    // Abre el formulario para añadir un libro nuevo o actualizar el
    // seleccionado, según el parámetro "actualizar".
    // Si es actualización, precarga los campos con los datos actuales.
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
        JTextField campoPaginas = new JTextField(15);
        JTextField campoPrecio = new JTextField(15);
        JTextField campoCantidad = new JTextField(15);
        JComboBox<String> campoFormato = new JComboBox<>(new String[]{"Físico", "Digital"});

        String[] etiquetas = {"ISBN", "Título", "Autor", "Género", "Editorial",
                "N° de Páginas", "Precio", "Cantidad", "Formato"};
        JComponent[] campos = {campoIsbn, campoTitulo, campoAutor, campoGenero, campoEditorial,
                campoPaginas, campoPrecio, campoCantidad, campoFormato};

        for (int i = 0; i < etiquetas.length; i++) {
            c.gridy = i * 2;
            dialogo.add(new JLabel(etiquetas[i]), c);
            c.gridy = i * 2 + 1;
            dialogo.add(campos[i], c);
        }

        if (actualizar) {
            campoIsbn.setText(existente.getIsbn());
            campoTitulo.setText(existente.getTitulo());
            campoAutor.setText(existente.getAutor());
            campoGenero.setText(existente.getCategoria());
            campoEditorial.setText(existente.getEditorial());
            campoPaginas.setText(String.valueOf(existente.getNumeroPaginas()));
            campoPrecio.setText(String.valueOf(existente.getPrecio()));
            campoCantidad.setText(String.valueOf(existente.getCantidadDisponible()));
            campoFormato.setSelectedItem(existente.getFormatoLibro());
        }

        JButton botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(e -> {
            if (campoTitulo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "El título es obligatorio.");
                return;
            }
            try {
                int paginas = campoPaginas.getText().trim().isEmpty() ? 0 : Integer.parseInt(campoPaginas.getText().trim());
                double precio = campoPrecio.getText().trim().isEmpty() ? 0 : Double.parseDouble(campoPrecio.getText().trim());
                int cantidad = campoCantidad.getText().trim().isEmpty() ? 0 : Integer.parseInt(campoCantidad.getText().trim());

                if (actualizar) {
                    existente.setIsbn(campoIsbn.getText().trim());
                    existente.setTitulo(campoTitulo.getText().trim());
                    existente.setAutor(campoAutor.getText().trim()); 
                    existente.setCategoria(campoGenero.getText().trim());
                    existente.setEditorial(campoEditorial.getText().trim());
                    existente.setNumeroPaginas(paginas);
                    existente.setPrecio(precio);
                    existente.setCantidadDisponible(cantidad);
                    existente.setFormatoLibro((String) campoFormato.getSelectedItem());
                    inventario.actualizarLibro(existente);
                } else {
                    Libro nuevo = new Libro(campoIsbn.getText().trim(), campoTitulo.getText().trim(),
                            campoAutor.getText().trim(), 0, campoGenero.getText().trim(),
                            campoEditorial.getText().trim(), paginas, precio, cantidad,
                            (String) campoFormato.getSelectedItem());
                    inventario.agregarLibro(nuevo);
                }
                cargarLibros();
                dialogo.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "N° de Páginas, Precio y Cantidad deben ser numéricos.");
            }
        });
        c.gridy = etiquetas.length * 2;
        dialogo.add(botonGuardar, c);

        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
    // Elimina el libro seleccionado del inventario, previa confirmación
    // del usuario.
    private void eliminarLibro() {
        Libro libro = libroSeleccionado();
        if (libro == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro para eliminar.");
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
