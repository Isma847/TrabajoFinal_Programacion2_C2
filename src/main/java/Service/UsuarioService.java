package Service;

import DAO.UsuarioDao;
import Enumerados.Rol;
import Excepciones.DatoInvalidoException;
import Modelo.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class UsuarioService {

    private final UsuarioDao dao = new UsuarioDao();
    private final Map<Integer, Usuario> coleccion = new LinkedHashMap<>();

    public UsuarioService() throws SQLException {
        for (Usuario usuario : dao.leerTodos()) {
            coleccion.put(usuario.getId(), usuario);
        }
    }

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

    public Usuario buscarPorId(int id) {
        Usuario usuario = coleccion.get(id);
        if (usuario == null) {
            return null;
        }

        return copiar(usuario);
    }

    private int contarAdministradoresActivos() {
        int cantidad = 0;

        for (Usuario usuario : coleccion.values()) {
            if (usuario.getRol() == Rol.ADMINISTRADOR && !usuario.getBaja()) {
                cantidad++;
            }
        }

        return cantidad;
    }

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
    public ArrayList<Usuario> leerTodos() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        for (Usuario usuario : coleccion.values()) {
            usuarios.add(copiar(usuario));
        }

        return usuarios;
    }

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

    private void validarTexto(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()
                || valor.codePointCount(0, valor.length()) > maximo) {
            throw new DatoInvalidoException(
                    "El campo " + campo + " debe tener entre 1 y " + maximo + " caracteres"
            );
        }
    }

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