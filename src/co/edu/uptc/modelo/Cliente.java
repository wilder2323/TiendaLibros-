package co.edu.uptc.modelo;

public class Cliente {
    private String nombreCompleto;
    private String correo;
    private String contraseña;
    private String direccionEnvio;
    private String telefono;
    private String tipoCliente;
// Representa al usuario que inicia sesión en la tienda.
// No se conecta a ninguna base de datos: solo valida que los campos
// del login no estén vacíos.
    public Cliente(String nombreCompleto, String correo, String contraseña,
                   String direccionEnvio, String telefono, String tipoCliente) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.contraseña = contraseña;
        this.direccionEnvio = direccionEnvio;
        this.telefono = telefono;
        this.tipoCliente = tipoCliente;
    }
// Valida las credenciales ingresadas. 
// Retorna true solo si tanto el nombre como la contraseña 
// fueron diligenciados.
    public boolean verificarContraseña(String contraseñaIngresada) {
        return contraseña != null && contraseña.equals(contraseñaIngresada);
    }

    public boolean esPremium() {
        return "Premium".equalsIgnoreCase(tipoCliente);
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }
}