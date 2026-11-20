package Service;

import Excepciones.DatoInvalidoException;
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

    // Devuelve true si la cuenta se creo; si no, informa el motivo por consola.
    public boolean agregarUsuario(Usuario usuario) {
        try {
            verificarDisponible(usuario);
            return usuarios.agregar(usuario);
        } catch (DatoInvalidoException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("No se pudo crear la cuenta: " + e.getMessage());
        }
        return false;
    }

    public boolean agregarAdministrador(Administrador admin) {
        try {
            verificarDisponible(admin);
            return administradores.agregar(admin);
        } catch (DatoInvalidoException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("No se pudo crear la cuenta: " + e.getMessage());
        }
        return false;
    }

    // username y email deben ser unicos entre usuarios Y administradores
    private void verificarDisponible(Cuenta nueva) {
        if (nueva == null) {
            throw new DatoInvalidoException("La cuenta es obligatoria");
        }
        ArrayList<Cuenta> cuentas = new ArrayList<>(usuarios.leerTodos());
        cuentas.addAll(administradores.leerTodos());
        for (Cuenta c : cuentas) {
            if (c.getUsername() != null && c.getUsername().equalsIgnoreCase(nueva.getUsername())) {
                throw new DatoInvalidoException("Ese nombre de usuario ya está en uso");
            }
            if (c.getEmail() != null && c.getEmail().equalsIgnoreCase(nueva.getEmail())) {
                throw new DatoInvalidoException("Ese email ya está registrado");
            }
        }
    }

}