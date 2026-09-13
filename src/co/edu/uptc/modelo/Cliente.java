package co.edu.uptc.modelo;

public class Cliente {
    private String nombreCompleto;
    private String contraseña;
// Representa al usuario que inicia sesión en la tienda.
// No se conecta a ninguna base de datos: solo valida que los campos
// del login no estén vacíos.
    public Cliente(String nombreCompleto, String contraseña) {
        this.nombreCompleto = nombreCompleto;
        this.contraseña = contraseña;
    }
// Valida las credenciales ingresadas. 
// Retorna true solo si tanto el nombre como la contraseña 
// fueron diligenciados.
    public boolean iniciarSesion() {
        return nombreCompleto != null && !nombreCompleto.isBlank()
                && contraseña != null && !contraseña.isBlank();
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
}
