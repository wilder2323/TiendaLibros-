package co.edu.uptc.gui;

import javax.swing.*;
import java.awt.*;
import co.edu.uptc.modelo.Cliente;
import co.edu.uptc.modelo.Carrito;
import co.edu.uptc.datos.Inventario;
import co.edu.uptc.datos.RepositorioClientes;
import co.edu.uptc.datos.RepositorioCompras;

// Ventana principal de la aplicación.
// Usa un CardLayout para alternar entre las 5 pantallas (login, registro,
// catálogo, carrito, perfil), mostrando solo una a la vez.
// También guarda las instancias compartidas (inventario, repositorios, carrito,
// cliente actual) para que todos los paneles trabajen sobre los mismos datos.
public class VentanaPrincipal extends JFrame {

    // CardLayout funciona como un mazo de cartas: apila paneles y muestra uno solo
    private CardLayout barraMenu = new CardLayout();
    private JPanel panelActual = new JPanel(barraMenu);

    private Inventario inventario = new Inventario();
    private RepositorioClientes repositorioClientes = new RepositorioClientes();
    private RepositorioCompras repositorioCompras = new RepositorioCompras();
    private Carrito carrito = new Carrito(); // un solo carrito compartido durante toda la sesión
    private Cliente clienteActual;

    private PanelInicioSesion panelInicioSesion;
    private PanelRegistro panelRegistro;
    private PanelCatalogo panelCatalogo;
    private PanelCarrito panelCarrito;
    private PanelPerfil panelPerfil;

    public VentanaPrincipal() {
        super("Tienda Virtual de Libros");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 500);
        setLocationRelativeTo(null);

        panelInicioSesion = new PanelInicioSesion(this, repositorioClientes);
        panelRegistro = new PanelRegistro(this, repositorioClientes);
        panelCatalogo = new PanelCatalogo(this, inventario);
        panelCarrito = new PanelCarrito(this, inventario, repositorioCompras);
        panelPerfil = new PanelPerfil(this, repositorioClientes, repositorioCompras);

        panelActual.add(panelInicioSesion, "login");
        panelActual.add(panelRegistro, "registro");
        panelActual.add(panelCatalogo, "catalogo");
        panelActual.add(panelCarrito, "carrito");
        panelActual.add(panelPerfil, "perfil");
        add(panelActual);

        mostrarPanelInicioSesion();
    }

    // Cambia la pantalla visible al panel de inicio de sesión
    public void mostrarPanelInicioSesion() {
        barraMenu.show(panelActual, "login");
    }

    // Cambia la pantalla visible al formulario de registro
    public void mostrarPanelRegistro() {
        barraMenu.show(panelActual, "registro");
    }

    // Recarga el listado de libros disponibles y muestra el catálogo
    public void mostrarPanelCatalogo() {
        panelCatalogo.cargarLibros();
        barraMenu.show(panelActual, "catalogo");
    }

    // Refresca la tabla y los totales del carrito antes de mostrarlo
    public void mostrarPanelCarrito() {
        panelCarrito.actualizarVista(clienteActual);
        barraMenu.show(panelActual, "carrito");
    }

    // Carga los datos del cliente logueado y su historial antes de mostrar el perfil
    public void mostrarPanelPerfil() {
        panelPerfil.cargarDatos(clienteActual);
        barraMenu.show(panelActual, "perfil");
    }

    public void setClienteActual(Cliente cliente) { this.clienteActual = cliente; }
    public Cliente getClienteActual() { return clienteActual; }

    // Estos getters permiten que los demás paneles usen las mismas instancias
    // en vez de crear sus propias copias (así todo queda sincronizado)
    public Carrito getCarrito() { return carrito; }
    public RepositorioCompras getRepositorioCompras() { return repositorioCompras; }
    public RepositorioClientes getRepositorioClientes() { return repositorioClientes; }

    // Punto de entrada del programa: crea y muestra la ventana principal
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}