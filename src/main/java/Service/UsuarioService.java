package Service;

import DAO.UsuarioDao;
import Enumerados.Rol;
import Excepciones.DatoInvalidoException;
import Modelo.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service de cuentas. Maneja usuarios y administradores en la misma clase, porque son la misma
 * entidad distinguida por el Rol. No extiende Service<T>: tiene su propia colección y
 * devuelve y guarda copias, de modo que un cambio solo queda si se hace por modificar,
 * que valida y escribe primero en la base.
 *
 * Reglas propias: validación de datos según el rol y protección del último administrador activo.
 * Como UsuarioDao no tiene bajaLogica, dar de baja una cuenta se hace con modificar (la baja es
 * un campo más).
 */
public class UsuarioService {

    private final UsuarioDao dao = new UsuarioDao();

    /**
     * Colección en memoria con el id como clave. Se usa un Map porque se busca y modifica por id
     * (acceso directo), y LinkedHashMap para mantener el orden de la base en los listados.
     */
    private final Map<Integer, Usuario> coleccion = new LinkedHashMap<>();

    /**
     * Carga todas las cuentas desde la base una sola vez.
     */
    public UsuarioService() throws SQLException {
        for (Usuario usuario : dao.leerTodos()) {
            coleccion.put(usuario.getId(), usuario);
        }
    }

    /**
     * Valida, rechaza la cuenta que ya trae un id, la guarda en la base y, si salió bien, guarda
     * una copia en la colección. La unicidad de username y email se controla en GestionCuenta.
     */
    public boolean agregar(Usuario usuario) throws SQLException {
        validar(usuario);
        if (usuario.getId() != 0) {
            return false;
        }

        int id = dao.crear(usuario);
        if (id <= 0) {
            return false;
        }

        usuario.setId(id);
        coleccion.put(id, copiar(usuario));
        return true;
    }

    /// Devuelve una copia de la cuenta, o null si no existe (también devuelve las dadas de baja).
    public Usuario buscarPorId(int id) {
        Usuario usuario = coleccion.get(id);
        if (usuario == null) {
            return null;
        }

        return copiar(usuario);
    }

    /// Cuenta los administradores que no están dados de baja.
    private int contarAdministradoresActivos() {
        int cantidad = 0;

        for (Usuario usuario : coleccion.values()) {
            if (usuario.getRol() == Rol.ADMINISTRADOR && !usuario.getBaja()) {
                cantidad++;
            }
        }

        return cantidad;
    }

    /**
     * Valida y actualiza la cuenta (incluida su baja). Si la cuenta es el único
     * administrador activo y el cambio lo dejaría de ser (por rol o por baja), lanza
     * DatoInvalidoException, para que el sistema siempre tenga al menos un administrador.
     */
    public boolean modificar(Usuario usuario) throws SQLException {
        validar(usuario);

        Usuario actual = coleccion.get(usuario.getId());
        if (actual == null) {
            return false;
        }

        boolean eraAdministradorActivo =
                actual.getRol() == Rol.ADMINISTRADOR && !actual.getBaja();

        boolean seguiraSiendoAdministradorActivo =
                usuario.getRol() == Rol.ADMINISTRADOR && !usuario.getBaja();

        if (eraAdministradorActivo
                && !seguiraSiendoAdministradorActivo
                && contarAdministradoresActivos() <= 1) {
            throw new DatoInvalidoException(
                    "Debe quedar al menos un administrador activo."
            );
        }

        if (dao.actualizar(usuario) <= 0) {
            return false;
        }

        coleccion.put(usuario.getId(), copiar(usuario));
        return true;
    }

    /// Devuelve una lista nueva con copias de todas las cuentas.
    public ArrayList<Usuario> leerTodos() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        for (Usuario usuario : coleccion.values()) {
            usuarios.add(copiar(usuario));
        }

        return usuarios;
    }

    /**
     * Reglas de la cuenta: textos obligatorios con el largo máximo de su columna, rol
     * obligatorio, edad obligatoria y entre 1 y 120 para el rol USUARIO, y el permiso de eliminar
     * solo para ADMINISTRADOR. Son las mismas reglas que los CHECK de la tabla.
     */
    private void validar(Usuario usuario) {
        if (usuario == null) {
            throw new DatoInvalidoException("El usuario es obligatorio");
        }

        validarTexto(usuario.getNombre(), "nombre", 80);
        validarTexto(usuario.getApellido(), "apellido", 80);
        validarTexto(usuario.getUsername(), "username", 60);
        validarTexto(usuario.getPassword(), "contraseña", 30);
        validarTexto(usuario.getEmail(), "email", 160);

        if (usuario.getRol() == null) {
            throw new DatoInvalidoException("El rol es obligatorio");
        }

        Integer edad = usuario.getEdad();
        if (usuario.getRol() == Rol.USUARIO && edad == null) {
            throw new DatoInvalidoException("La edad es obligatoria para el rol USUARIO");
        }

        if (edad != null && (edad < 1 || edad > 120)) {
            throw new DatoInvalidoException("La edad debe estar entre 1 y 120");
        }

        if (usuario.getRol() == Rol.USUARIO && usuario.isPuedeEliminar()) {
            throw new DatoInvalidoException(
                    "El permiso de eliminación corresponde al rol ADMINISTRADOR"
            );
        }
    }

    /// Verifica que un texto no sea null ni vacío y no pase el máximo de caracteres.
    private void validarTexto(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()
                || valor.codePointCount(0, valor.length()) > maximo) {
            throw new DatoInvalidoException(
                    "El campo " + campo + " debe tener entre 1 y " + maximo + " caracteres"
            );
        }
    }

    /**
     * Crea una cuenta nueva con los mismos datos (incluidos id y baja) para no exponer el objeto
     * guardado en la colección.
     */
    private Usuario copiar(Usuario usuario) {
        Usuario copia = new Usuario(
                usuario.getApellido(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getUsername(),
                usuario.getEdad(),
                usuario.getRol(),
                usuario.isPuedeEliminar()
        );

        copia.setId(usuario.getId());
        copia.setBaja(usuario.getBaja());
        return copia;
    }
}