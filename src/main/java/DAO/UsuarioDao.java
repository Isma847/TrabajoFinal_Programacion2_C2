package DAO;

import Conexion.ConexionDB;
import Enumerados.Rol;
import Interfaces.CRUDBase;
import Modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

/**
 * DAO de cuentas: tabla 'usuarios', que guarda tanto usuarios como administradores, que se
 * los distingue la columna rol. No implementa BajaLogicaEnDao para facilitar la rehabilitación
 * de cuentas que se dieron de baja. La baja es una columna más que se lee y se guarda junto con el
 * resto de los datos en actualizar. La edad es NULL para los administradores, por eso se usa
 * setNull al guardar y getObject(..., Integer.class) al leer (getInt devolvería 0).
 */
public class UsuarioDao implements CRUDBase<Usuario> {

    /**
     * Inserta la cuenta y devuelve su id generado, o -1 si no se insertó.
     * Realiza commit si sale bien, rollback si falla.
     */
    @Override
    public int crear(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, apellido, username, contrasena, email, "
                + "edad, rol, puedeEliminar, baja) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.conectar()) {
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                cargarParametros(ps, usuario);

                if (ps.executeUpdate() == 0) {
                    cn.rollback();
                    return -1;
                }

                int id;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("No se pudo obtener el ID del usuario");
                    }
                    id = rs.getInt(1);
                }

                cn.commit();
                return id;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    /// Lee todas las cuentas, incluidas las dadas de baja; el Service la usa al iniciarse.
    @Override
    public Collection<Usuario> leerTodos() throws SQLException {
        String sql = "SELECT id_usuario, nombre, apellido, username, contrasena, email, "
                + "edad, rol, puedeEliminar, baja FROM usuarios";
        ArrayList<Usuario> usuarios = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getString("apellido"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("contrasena"),
                        rs.getString("username"),
                        rs.getObject("edad", Integer.class),
                        Rol.valueOf(rs.getString("rol")),
                        rs.getBoolean("puedeEliminar")
                );

                usuario.setId(rs.getInt("id_usuario"));
                usuario.setBaja(rs.getBoolean("baja"));
                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    /**
     * Actualiza todos los datos de la cuenta por id, incluida la baja. Los nueve primeros
     * parámetros salen de cargarParametros y el id va último (posición 10).
     */
    @Override
    public int actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, username = ?, "
                + "contrasena = ?, email = ?, edad = ?, rol = ?, puedeEliminar = ?, baja = ? "
                + "WHERE id_usuario = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            cargarParametros(ps, usuario);
            ps.setInt(10, usuario.getId());

            return ps.executeUpdate();
        }
    }

    /// Borrado físico de la cuenta; la base borra por cascada sus dependencias.
    @Override
    public int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);

            return ps.executeUpdate();
        }
    }

    /// Carga los nueve datos que comparten el INSERT y el UPDATE, en el mismo orden de columnas.
    private void cargarParametros(PreparedStatement ps, Usuario usuario) throws SQLException {
        ps.setString(1, usuario.getNombre());
        ps.setString(2, usuario.getApellido());
        ps.setString(3, usuario.getUsername());
        ps.setString(4, usuario.getPassword());
        ps.setString(5, usuario.getEmail());

        if (usuario.getEdad() == null) {
            ps.setNull(6, Types.INTEGER);
        } else {
            ps.setInt(6, usuario.getEdad());
        }

        ps.setString(7, usuario.getRol().name());
        ps.setBoolean(8, usuario.isPuedeEliminar());
        ps.setBoolean(9, usuario.getBaja());
    }
}