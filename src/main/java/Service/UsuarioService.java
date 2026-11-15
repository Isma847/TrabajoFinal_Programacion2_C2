package Service;

import DAO.UsuarioDao;
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

    public boolean modificar(Usuario usuario) throws SQLException {
        validar(usuario);
        int id = usuario.getId();
        if (!coleccion.containsKey(id)) {
            return false;
        }

        if (dao.actualizar(usuario) <= 0) {
            return false;
        }

        coleccion.put(id, copiar(usuario));
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
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        if (usuario.getBaja()) {
            throw new IllegalArgumentException(
                    "El esquema actual solo permite guardar usuarios activos"
            );
        }

        validarTexto(usuario.getNombre(), "nombre", 80);
        validarTexto(usuario.getApellido(), "apellido", 80);
        validarTexto(usuario.getUsername(), "username", 60);
        validarTexto(usuario.getPassword(), "contraseña", 512);
        validarTexto(usuario.getEmail(), "email", 160);
    }

    private void validarTexto(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank() || valor.codePointCount(0, valor.length()) > maximo) {
            throw new IllegalArgumentException("El campo " + campo + " debe tener entre 1 y "
                    + maximo + " caracteres");
        }
    }

    private Usuario copiar(Usuario usuario) {
        Usuario copia = new Usuario(
                usuario.getApellido(),
                usuario.getNombre(),
                usuario.getBaja(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getUsername(),
                usuario.getEdad()
        );
        copia.setId(usuario.getId());
        return copia;
    }
}