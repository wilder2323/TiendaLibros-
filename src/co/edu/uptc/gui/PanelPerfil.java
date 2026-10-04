package co.edu.uptc.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import co.edu.uptc.modelo.Cliente;
import co.edu.uptc.modelo.Compra;
import co.edu.uptc.datos.RepositorioClientes;
import co.edu.uptc.datos.RepositorioCompras;

// Pantalla de perfil: permite ver y editar los datos personales del cliente,
// y muestra su historial de compras.
public class PanelPerfil extends JPanel {

    private VentanaPrincipal ventana;
    private RepositorioClientes repositorioClientes;
    private RepositorioCompras repositorioCompras;

    private JTextField campoNombre = new JTextField(18);
    private JTextField campoCorreo = new JTextField(18);
    private JTextField campoDireccion = new JTextField(18);
    private JTextField campoTelefono = new JTextField(18);
    private JLabel etiquetaTipoCliente = new JLabel();
    private JLabel etiquetaError = new JLabel(" ");

    private DefaultTableModel modeloHistorial;
    private JTable tablaHistorial;

    public PanelPerfil(VentanaPrincipal ventana, RepositorioClientes repositorioClientes, RepositorioCompras repositorioCompras) {
        this.ventana = ventana;
        this.repositorioClientes = repositorioClientes;
        this.repositorioCompras = repositorioCompras;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.add(new JLabel("Mi Perfil"), BorderLayout.WEST);
        JButton botonVolver = new JButton("Volver al Catálogo");
        botonVolver.addActionListener(e -> ventana.mostrarPanelCatalogo());
        JButton botonCerrarSesion = new JButton("Cerrar sesión");
        botonCerrarSesion.addActionListener(e -> cerrarSesion());
        JPanel botonesEncabezado = new JPanel();
        botonesEncabezado.add(botonVolver);
        botonesEncabezado.add(botonCerrarSesion);
        encabezado.add(botonesEncabezado, BorderLayout.EAST);
        add(encabezado, BorderLayout.NORTH);

        JPanel panelDatos = new JPanel(new GridBagLayout());
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos personales"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;

        c.gridy = 0; panelDatos.add(new JLabel("Nombre Completo"), c);
        c.gridy = 1; panelDatos.add(campoNombre, c);
        c.gridy = 2; panelDatos.add(new JLabel("Correo electrónico"), c);
        c.gridy = 3; panelDatos.add(campoCorreo, c);
        c.gridy = 4; panelDatos.add(new JLabel("Dirección de envío"), c);
        c.gridy = 5; panelDatos.add(campoDireccion, c);
        c.gridy = 6; panelDatos.add(new JLabel("Teléfono"), c);
        c.gridy = 7; panelDatos.add(campoTelefono, c);
        c.gridy = 8; panelDatos.add(new JLabel("Tipo de cliente"), c);
        c.gridy = 9; panelDatos.add(etiquetaTipoCliente, c);

        etiquetaError.setForeground(Color.RED);
        c.gridy = 10; panelDatos.add(etiquetaError, c);

        JButton botonGuardar = new JButton("Guardar cambios");
        botonGuardar.addActionListener(e -> guardarCambios());
        c.gridy = 11; panelDatos.add(botonGuardar, c);

        add(panelDatos, BorderLayout.WEST);

        modeloHistorial = new DefaultTableModel(new String[]{"N° Compra", "Fecha", "Total", "Método de pago"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
        tablaHistorial = new JTable(modeloHistorial);
        JPanel panelHistorial = new JPanel(new BorderLayout());
        panelHistorial.setBorder(BorderFactory.createTitledBorder("Historial de Compras"));
        panelHistorial.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);
        add(panelHistorial, BorderLayout.CENTER);
    }

    // Llena los campos con los datos del cliente logueado y carga
    // únicamente las compras que le pertenecen a él
    public void cargarDatos(Cliente cliente) {
        if (cliente == null) return;
        campoNombre.setText(cliente.getNombreCompleto());
        campoCorreo.setText(cliente.getCorreo());
        campoDireccion.setText(cliente.getDireccionEnvio());
        campoTelefono.setText(cliente.getTelefono());
        etiquetaTipoCliente.setText(cliente.getTipoCliente());
        etiquetaError.setText(" ");

        modeloHistorial.setRowCount(0);
        for (Compra compra : repositorioCompras.historialDeCliente(cliente)) {
            modeloHistorial.addRow(new Object[]{
                    compra.getIdCompra(),
                    compra.getFechaCompra(),
                    String.format("$%.0f", compra.getTotalCompra()),
                    compra.getMetodoPago()
            });
        }
    }

    // Valida los nuevos datos (igual que en el registro, pero sin tocar la
    // contraseña) y verifica que, si cambia el correo, no choque con otro cliente
    private void guardarCambios() {
        Cliente cliente = ventana.getClienteActual();
        if (cliente == null) return;

        String nombre = campoNombre.getText().trim();
        String correo = campoCorreo.getText().trim();
        String direccion = campoDireccion.getText().trim();
        String telefono = campoTelefono.getText().trim();

        if (nombre.isEmpty()) { etiquetaError.setText("El nombre completo es obligatorio."); return; }
        if (correo.isEmpty() || !correo.contains("@") || !correo.contains(".")) {
            etiquetaError.setText("Ingresa un correo válido.");
            return;
        }
        if (!correo.equalsIgnoreCase(cliente.getCorreo()) && repositorioClientes.correoRegistrado(correo)) {
            etiquetaError.setText("Ese correo ya está registrado por otro cliente.");
            return;
        }
        if (direccion.isEmpty()) { etiquetaError.setText("La dirección de envío es obligatoria."); return; }
        if (!telefono.matches("\\d{7,15}")) {
            etiquetaError.setText("Ingresa un teléfono válido (solo números).");
            return;
        }

        cliente.setNombreCompleto(nombre);
        cliente.setCorreo(correo);
        cliente.setDireccionEnvio(direccion);
        cliente.setTelefono(telefono);

        etiquetaError.setText(" ");
        JOptionPane.showMessageDialog(this, "Datos actualizados correctamente.");
    }

    // Cierra sesión, vacía el carrito (ya no es del cliente que se va)
    // y vuelve a la pantalla de login
    private void cerrarSesion() {
        ventana.setClienteActual(null);
        ventana.getCarrito().vaciar();
        ventana.mostrarPanelInicioSesion();
    }
}