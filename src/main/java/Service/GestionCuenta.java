package Service;

import Excepciones.DatoInvalidoException;
import Excepciones.SesionInvalidaException;
import Modelo.Usuario;

import java.sql.SQLException;

public class GestionCuenta {

    private final UsuarioService usuarios;
    private Usuario usuarioActual;

    public GestionCuenta(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    public void iniciarSesion(String username, String password) throws SesionInvalidaException {
        usuarioActual = null;
        Usuario cuentaEncontrada = null;

        for (Usuario usuario : usuarios.leerTodos()) {
            if (!usuario.getBaja()
                    && usuario.getUsername().equals(username)
                    && usuario.getPassword().equals(password)) {
                if (cuentaEncontrada != null) {
                    throw new SesionInvalidaException("Las credenciales coinciden con más de una cuenta");
                }
                cuentaEncontrada = usuario;
            }
        }

        if (cuentaEncontrada == null) {
            throw new SesionInvalidaException("Usuario o contraseña incorrectos");
        }

        usuarioActual = cuentaEncontrada;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public boolean agregarUsuario(Usuario usuario) {
        try {
            verificarDisponible(usuario);
            return usuarios.agregar(usuario);
        } catch (SQLException e) {
            System.out.println("No se pudo crear la cuenta: " + e.getMessage());
        }
        return false;
    }

    private void verificarDisponible(Usuario nueva) {
        if (nueva == null) {
            throw new DatoInvalidoException("La cuenta es obligatoria");
        }

        for (Usuario usuario : usuarios.leerTodos()) {
            if (usuario.getUsername() != null
                    && usuario.getUsername().equalsIgnoreCase(nueva.getUsername())) {
                throw new DatoInvalidoException("Ese nombre de usuario ya está en uso");
            }
            if (usuario.getEmail() != null && usuario.getEmail().equalsIgnoreCase(nueva.getEmail())) {
                throw new DatoInvalidoException("Ese email ya está registrado");
            }
        }
    }
}

