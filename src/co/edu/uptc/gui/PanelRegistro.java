package co.edu.uptc.gui;

import javax.swing.*;
import java.awt.*;
import co.edu.uptc.modelo.Cliente;
import co.edu.uptc.datos.RepositorioClientes;

// Formulario para que un cliente nuevo se registre en la tienda.
// Valida cada campo según las reglas mínimas del documento antes de crear el Cliente.
public class PanelRegistro extends JPanel {

    private JTextField campoNombre = new JTextField(18);
    private JTextField campoCorreo = new JTextField(18);
    private JPasswordField campoContraseña = new JPasswordField(18);
    private JTextField campoDireccion = new JTextField(18);
    private JTextField campoTelefono = new JTextField(18);
    private JComboBox<String> campoTipoCliente = new JComboBox<>(new String[]{"Regular", "Premium"});
    private JLabel etiquetaError = new JLabel(" ");
    private VentanaPrincipal ventana;
    private RepositorioClientes repositorioClientes;

    public PanelRegistro(VentanaPrincipal ventana, RepositorioClientes repositorioClientes) {
        this.ventana = ventana;
        this.repositorioClientes = repositorioClientes;
        construirInterfaz();
    }

private void construirInterfaz() {
    setLayout(new GridBagLayout());

    JPanel caja = new JPanel(new GridBagLayout());
    caja.setBorder(BorderFactory.createTitledBorder("Registrarse"));
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 6, 5, 6);
    c.anchor = GridBagConstraints.WEST;
    c.gridx = 0;
    c.fill = GridBagConstraints.HORIZONTAL; 
    c.weightx = 1.0;                          

    String[] etiquetas = {"Nombre Completo", "Correo electrónico", "Contraseña",
            "Dirección de envío", "Teléfono", "Tipo de cliente"};
    JComponent[] campos = {campoNombre, campoCorreo, campoContraseña,
            campoDireccion, campoTelefono, campoTipoCliente};

    for (int i = 0; i < etiquetas.length; i++) {
        c.gridy = i * 2;
        caja.add(new JLabel(etiquetas[i]), c);
        c.gridy = i * 2 + 1;
        caja.add(campos[i], c);
    
        }

        etiquetaError.setForeground(Color.RED);
        c.gridy = etiquetas.length * 2;
        caja.add(etiquetaError, c);

        JButton botonRegistrar = new JButton("Registrar");
        botonRegistrar.addActionListener(e -> registrar());
        c.gridy = etiquetas.length * 2 + 1;
        caja.add(botonRegistrar, c);

        JButton botonVolver = new JButton("Volver al inicio de sesión");
        botonVolver.addActionListener(e -> {
            limpiarCampos();
            ventana.mostrarPanelInicioSesion();
        });
        c.gridy = etiquetas.length * 2 + 2;
        caja.add(botonVolver, c);

        add(caja);
    }

    // Revisa cada campo uno por uno (nombre, correo, duplicado, dirección,
    // teléfono, longitud de contraseña) antes de crear y guardar el Cliente
    private void registrar() {
        String nombre = campoNombre.getText().trim();
        String correo = campoCorreo.getText().trim();
        String contraseña = new String(campoContraseña.getPassword()).trim();
        String direccion = campoDireccion.getText().trim();
        String telefono = campoTelefono.getText().trim();
        String tipoCliente = (String) campoTipoCliente.getSelectedItem();

        if (nombre.isEmpty()) {
            etiquetaError.setText("El nombre completo es obligatorio.");
            return;
        }
        if (correo.isEmpty() || !correo.contains("@") || !correo.contains(".")) {
            etiquetaError.setText("Ingresa un correo válido.");
            return;
        }
        if (repositorioClientes.correoRegistrado(correo)) {
            etiquetaError.setText("Ese correo ya está registrado.");
            return;
        }
        if (direccion.isEmpty()) {
            etiquetaError.setText("La dirección de envío es obligatoria.");
            return;
        }
        if (!telefono.matches("\\d{7,15}")) {
            etiquetaError.setText("Ingresa un teléfono válido (solo números).");
            return;
        }
        if (contraseña.length() < 6) {
            etiquetaError.setText("La contraseña debe tener mínimo 6 caracteres.");
            return;
        }

        Cliente nuevo = new Cliente(nombre, correo, contraseña, direccion, telefono, tipoCliente);
        repositorioClientes.registrarCliente(nuevo);

        limpiarCampos();
        JOptionPane.showMessageDialog(this, "Cliente registrado con éxito. Ahora puedes iniciar sesión.");
        ventana.mostrarPanelInicioSesion();
    }

    private void limpiarCampos() {
        campoNombre.setText("");
        campoCorreo.setText("");
        campoContraseña.setText("");
        campoDireccion.setText("");
        campoTelefono.setText("");
        campoTipoCliente.setSelectedIndex(0);
        etiquetaError.setText(" ");
    }
}