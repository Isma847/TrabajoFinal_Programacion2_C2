package Service;

import Excepciones.DatoInvalidoException;
import Excepciones.SesionInvalidaException;
import Modelo.Usuario;

import java.sql.SQLException;

/**
 * Maneja la sesión y las operaciones de una persona sobre su propia cuenta: iniciar y cerrar
 * sesión, crear una cuenta y consultar, modificar o dar de baja la cuenta actual. No tiene
 * colección propia: trabaja sobre UsuarioService.
 *
 * Los permisos por rol (qué puede hacer un administrador o un usuario) no se controlan acá, sino
 * en los menús y pantallas, porque esta clase solo sabe quién inició sesión.
 */
public class GestionCuenta {

    private final UsuarioService usuarios;

    /**
     * Cuenta con la sesión iniciada, o null si no hay ninguna.
     */
    private Usuario usuarioActual;

    public GestionCuenta(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * Busca una cuenta activa (no dada de baja) con ese username y contraseña. Si no hay ninguna
     * o hay más de una lanza SesionInvalidaException (excepción verificada, hay que capturarla).
     */
    public void iniciarSesion(String email, String password) throws SesionInvalidaException {
        usuarioActual = null;
        Usuario cuentaEncontrada = null;

        for (Usuario usuario : usuarios.leerTodos()) {
            if (!usuario.getBaja()
                    && usuario.getEmail().equals(email)
                    && usuario.getPassword().equals(password)) {
                if (cuentaEncontrada != null) {
                    throw new SesionInvalidaException("Las credenciales coinciden con más de una cuenta");
                }
                cuentaEncontrada = usuario;
            }
        }

        if (cuentaEncontrada == null) {
            throw new SesionInvalidaException("Correo o contraseña incorrectos");
        }

        usuarioActual = cuentaEncontrada;
    }

    /**
     * Devuelve la cuenta con sesión iniciada, o null.
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Cierra la sesión actual.
     */
    public void cerrarSesion() {
        usuarioActual = null;
    }

    /**
     * Crea una cuenta nueva. Primero verifica que username y email estén libres y recién ahí la
     * delega en UsuarioService. Los datos inválidos propagan DatoInvalidoException; los errores
     * de la base se muestran por consola y devuelven false.
     */
    public boolean agregarUsuario(Usuario usuario) {
        try {
            verificarDisponible(usuario);
            return usuarios.agregar(usuario);
        } catch (SQLException e) {
            System.out.println("No se pudo crear la cuenta: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verifica que ningún otro usuario tenga el mismo username o email (sin distinguir
     * mayúsculas). Se saltea la propia cuenta comparando por id, así se puede modificar una
     * cuenta sin que choque consigo misma. Lanza DatoInvalidoException si ya están en uso.
     */
    private void verificarDisponible(Usuario nueva) {
        if (nueva == null) {
            throw new DatoInvalidoException("La cuenta es obligatoria.");
        }

        for (Usuario usuario : usuarios.leerTodos()) {
            if (usuario.getId() == nueva.getId()) {
                continue;
            }

            if (usuario.getUsername() != null
                    && usuario.getUsername().equalsIgnoreCase(nueva.getUsername())) {
                throw new DatoInvalidoException(
                        "Ese nombre de usuario ya está en uso."
                );
            }

            if (usuario.getEmail() != null
                    && usuario.getEmail().equalsIgnoreCase(nueva.getEmail())) {
                throw new DatoInvalidoException(
                        "Ese email ya está registrado."
                );
            }
        }
    }

    /**
     * Devuelve una copia actualizada de la cuenta con sesión iniciada. Si no hay sesión lanza
     * DatoInvalidoException; si la cuenta ya no existe o fue dada de baja, cierra la sesión y
     * lanza la excepción.
     */
    public Usuario consultarMiCuenta() {
        if (usuarioActual == null) {
            throw new DatoInvalidoException("Debés iniciar sesión.");
        }

        Usuario usuario = usuarios.buscarPorId(usuarioActual.getId());

        if (usuario == null || usuario.getBaja()) {
            cerrarSesion();
            throw new DatoInvalidoException("Tu cuenta no está disponible.");
        }

        return usuario;
    }

    /**
     * Modifica los datos personales de la cuenta actual (nombre, apellido, username, email,
     * contraseña y edad). El rol, la baja y los permisos no se pueden cambiar desde acá. Trabaja
     * sobre una copia y, si todo sale bien, actualiza la cuenta de la sesión.
     */
    public boolean modificarMiCuenta(Usuario cambios) throws SQLException {
        if (cambios == null) {
            throw new DatoInvalidoException("Los datos son obligatorios.");
        }

        Usuario usuario = consultarMiCuenta();

        // Solo se copian los datos personales permitidos.
        usuario.setNombre(cambios.getNombre());
        usuario.setApellido(cambios.getApellido());
        usuario.setUsername(cambios.getUsername());
        usuario.setEmail(cambios.getEmail());
        usuario.setPassword(cambios.getPassword());
        usuario.setEdad(cambios.getEdad());

        verificarDisponible(usuario);

        if (!usuarios.modificar(usuario)) {
            return false;
        }

        usuarioActual = usuarios.buscarPorId(usuario.getId());
        return true;
    }

    /**
     * Da de baja la cuenta actual y cierra la sesión. Solo vale para el rol USUARIO: los
     * administradores no pueden darse de baja a sí mismos.
     */
    public boolean darDeBajaMiCuenta() throws SQLException {
        Usuario usuario = consultarMiCuenta();

        if (usuario.getRol() != Enumerados.Rol.USUARIO) {
            throw new DatoInvalidoException(
                    "Los administradores no pueden darse de baja a sí mismos."
            );
        }

        usuario.setBaja(true);

        if (!usuarios.modificar(usuario)) {
            return false;
        }

        cerrarSesion();
        return true;
    }

}

