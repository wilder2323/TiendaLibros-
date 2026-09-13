package co.edu.uptc.gui;

import javax.swing.*;
import java.awt.*;
import co.edu.uptc.modelo.Cliente;
// Pantalla de login: pide nombre completo y contraseña.
// No valida contra una base de datos, delega la validación al objeto Cliente.
public class PanelInicioSesion extends JPanel {

    private JTextField campoNombre = new JTextField(15);
    private JPasswordField campoContraseña = new JPasswordField(15);
    private JLabel etiquetaError = new JLabel(" ");
    private VentanaPrincipal ventana;

    public PanelInicioSesion(VentanaPrincipal ventana) {
        this.ventana = ventana;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new GridBagLayout());

        JPanel caja = new JPanel(new GridBagLayout());
        caja.setBorder(BorderFactory.createTitledBorder("Tienda de Libros"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;

        c.gridy = 0; caja.add(new JLabel("Nombre Completo"), c);
        c.gridy = 1; caja.add(campoNombre, c);
        c.gridy = 2; caja.add(new JLabel("Contraseña"), c);
        c.gridy = 3; caja.add(campoContraseña, c);

        etiquetaError.setForeground(Color.RED); 
        c.gridy = 4; caja.add(etiquetaError, c);

        JButton botonIniciarSesion = new JButton("Iniciar sesión");
        botonIniciarSesion.addActionListener(e -> autenticar());
        c.gridy = 5; caja.add(botonIniciarSesion, c);

        add(caja);
    }

    private void autenticar() {
        String nombre = campoNombre.getText().trim();
        String contraseña = new String(campoContraseña.getPassword()).trim();
    // Crea un Cliente con los datos ingresados y le pregunta si puede
    // iniciar sesión. Si es válido, se lo entrega a la ventana principal
    // y se cambia a la pantalla del catálogo; si no, muestra un error.

        Cliente cliente = new Cliente(nombre, contraseña);
        if (cliente.iniciarSesion()) {
            etiquetaError.setText(" ");
            ventana.setClienteActual(cliente);
            ventana.mostrarPanelCatalogo();
        } else {
            etiquetaError.setText("Ingresa nombre y contraseña.");
        }
    }
}
