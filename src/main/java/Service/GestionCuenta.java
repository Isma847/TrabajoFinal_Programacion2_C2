package Service;

import Excepciones.SesionInvalidaException;
import Modelo.Administrador;
import Modelo.Cuenta;
import Modelo.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;

public class GestionCuenta {

    private final UsuarioService usuarios;
    private final AdministradorService administradores;
    private Cuenta usuarioActual;

    public GestionCuenta(UsuarioService usuarios, AdministradorService administradores) {
        this.usuarios = usuarios;
        this.administradores = administradores;
    }

    public void iniciarSesion(String username, String password) throws SesionInvalidaException {
        usuarioActual = null;

        ArrayList<Cuenta> cuentas = new ArrayList<>(usuarios.leerTodos());
        cuentas.addAll(administradores.leerTodos());

        Cuenta cuentaEncontrada = null;
        for (Cuenta cuenta : cuentas) {
            if (!cuenta.getBaja()
                    && cuenta.getUsername().equals(username)
                    && cuenta.getPassword().equals(password)) {
                if (cuentaEncontrada != null) {
                    throw new SesionInvalidaException("Las credenciales coinciden con más de una cuenta");
                }
                cuentaEncontrada = cuenta;
            }
        }

        if (cuentaEncontrada == null) {
            throw new SesionInvalidaException("Usuario o contraseña incorrectos");
        }

        usuarioActual = cuentaEncontrada;
    }

    public Cuenta getUsuarioActual() {
        return usuarioActual;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public void agregarUsuario(Usuario usuario){
        try {
            usuarios.agregar(usuario);
        } catch (SQLException e) {
            System.err.println("error al crear el usuario: " +  e.getMessage());
            e.printStackTrace();
        }
    }

    public void agregarAdministrador(Administrador admin){
        try {
            administradores.agregar(admin);
        } catch (SQLException e) {
            System.err.println("error al crear el usuario: " +  e.getMessage());
            e.printStackTrace();
        }
    }

}