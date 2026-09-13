package co.edu.uptc.gui;

import javax.swing.*;
import java.awt.*;
import co.edu.uptc.modelo.Cliente;
import co.edu.uptc.datos.Inventario;
// Ventana principal de la aplicación.
// Usa un CardLayout para alternar entre la pantalla de login
// y la pantalla del catálogo, mostrando solo una a la vez.
public class VentanaPrincipal extends JFrame { 

    private CardLayout barraMenu = new CardLayout();
    private JPanel panelActual = new JPanel(barraMenu);
// CardLayout funciona como un mazo de cartas: apila paneles y muestra uno solo
    private Inventario inventario = new Inventario();
    private Cliente clienteActual;
// Guarda el cliente que inició sesión, para saber quién está usando la app
    private PanelInicioSesion panelInicioSesion;
    private PanelCatalogo panelCatalogo;
// Cambia la pantalla visible al panel de inicio de sesión
    public VentanaPrincipal() {
        super("Tienda de Libros");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 420);
        setLocationRelativeTo(null);

        panelInicioSesion = new PanelInicioSesion(this);
        panelCatalogo = new PanelCatalogo(this, inventario);

        panelActual.add(panelInicioSesion, "login");
        panelActual.add(panelCatalogo, "catalogo");
        add(panelActual);

        mostrarPanelInicioSesion();
    }

    public void mostrarPanelInicioSesion() {
        barraMenu.show(panelActual, "login");
    }

    public void mostrarPanelCatalogo() {// Recarga el listado de libros y cambia la pantalla visible al catálogo
        panelCatalogo.cargarLibros();
        barraMenu.show(panelActual, "catalogo");
    }

    public void setClienteActual(Cliente cliente) { this.clienteActual = cliente; }
    public Cliente getClienteActual() { return clienteActual; }

    public static void main(String[] args) {  // Punto de entrada del programa: crea y muestra la ventana principal
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
