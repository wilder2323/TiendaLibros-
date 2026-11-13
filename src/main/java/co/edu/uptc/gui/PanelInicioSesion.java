package co.edu.uptc.gui;

import javax.swing.*;
import java.awt.*;
import co.edu.uptc.modelo.Cliente;
import co.edu.uptc.datos.RepositorioClientes;

// Pantalla de login: pide correo y contraseña.
// A diferencia de la versión anterior, ahora sí valida contra los clientes
// ya registrados en el RepositorioClientes (no cualquiera puede entrar).
public class PanelInicioSesion extends JPanel {

    private JTextField campoCorreo = new JTextField(18);
    private JPasswordField campoContraseña = new JPasswordField(18);
    private JLabel etiquetaError = new JLabel(" ");
    private VentanaPrincipal ventana;
    private RepositorioClientes repositorioClientes;

    public PanelInicioSesion(VentanaPrincipal ventana, RepositorioClientes repositorioClientes) {
        this.ventana = ventana;
        this.repositorioClientes = repositorioClientes;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new GridBagLayout());

        JPanel caja = new JPanel(new GridBagLayout());
        caja.setBorder(BorderFactory.createTitledBorder("Tienda Virtual de Libros"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;

        c.gridy = 0; caja.add(new JLabel("Correo electrónico"), c);
        c.gridy = 1; caja.add(campoCorreo, c);
        c.gridy = 2; caja.add(new JLabel("Contraseña"), c);
        c.gridy = 3; caja.add(campoContraseña, c);

        etiquetaError.setForeground(Color.RED);
        c.gridy = 4; caja.add(etiquetaError, c);

        JButton botonIniciarSesion = new JButton("Iniciar sesión");
        botonIniciarSesion.addActionListener(e -> autenticar());
        c.gridy = 5; caja.add(botonIniciarSesion, c);

        JButton botonRegistrarse = new JButton("Registrarse");
        botonRegistrarse.addActionListener(e -> {
            limpiarCampos();
            ventana.mostrarPanelRegistro();
        });
        c.gridy = 6; caja.add(botonRegistrarse, c);

        add(caja);
    }

    // Valida el formato del correo, que la contraseña no esté vacía,
    // y luego le pregunta al repositorio si esas credenciales son correctas
    private void autenticar() {
        String correo = campoCorreo.getText().trim();
        String contraseña = new String(campoContraseña.getPassword()).trim();

        if (correo.isEmpty() || !correo.contains("@") || !correo.contains(".")) {
            etiquetaError.setText("Ingresa un correo válido.");
            return;
        }
        if (contraseña.isEmpty()) {
            etiquetaError.setText("La contraseña es obligatoria.");
            return;
        }

        Cliente cliente = repositorioClientes.autenticar(correo, contraseña);
        if (cliente == null) {
            etiquetaError.setText("Correo o contraseña incorrectos.");
            return;
        }

        ventana.setClienteActual(cliente);
        limpiarCampos();
        ventana.mostrarPanelCatalogo();
    }

    // Limpia los campos y el mensaje de error, para que la próxima vez
    // que se muestre este panel no queden datos de la sesión anterior
    public void limpiarCampos() {
        campoCorreo.setText("");
        campoContraseña.setText("");
        etiquetaError.setText(" ");
    }
}