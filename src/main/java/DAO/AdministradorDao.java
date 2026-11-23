package DAO;

import Conexion.ConexionDB;
import Interfaces.CRUDBase;
import Modelo.Administrador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class AdministradorDao implements CRUDBase<Administrador> {

    public int crear(Administrador administrador) throws SQLException {
        String sql = "INSERT INTO administradores (nombre, apellido, username, contrasena, email, puedeEliminar, baja) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlId = "SELECT LAST_INSERT_ID()";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             PreparedStatement psId = cn.prepareStatement(sqlId)) {
            cargarParametros(ps, administrador);

            if (ps.executeUpdate() == 0) {
                return -1;
            }

            try (ResultSet rs = psId.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("No se pudo obtener el ID del administrador");
        }
    }

    public Collection<Administrador> leerTodos() throws SQLException {
        String sql = "SELECT id_administrador, nombre, apellido, username, contrasena, email, puedeEliminar, baja "
                + "FROM administradores";
        ArrayList<Administrador> administradores = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Administrador administrador = new Administrador(
                        rs.getString("apellido"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("contrasena"),
                        rs.getString("username"),
                        false
                );
                administrador.setId(rs.getInt("id_administrador"));
                administrador.setBaja(rs.getBoolean("baja"));
                administradores.add(administrador);
            }
        }

        return administradores;
    }

    public int actualizar(Administrador administrador) throws SQLException {
        String sql = "UPDATE administradores SET nombre = ?, apellido = ?, username = ?, "
                + "contrasena = ?, email = ?, puedeEliminar = ?, baja = ? WHERE id_administrador = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            cargarParametros(ps, administrador);
            ps.setInt(8, administrador.getId());

            return ps.executeUpdate();
        }
    }
    @Override
    public int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM administradores WHERE id_administrador = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    private void cargarParametros(PreparedStatement ps, Administrador administrador) throws SQLException {
        ps.setString(1, administrador.getNombre());
        ps.setString(2, administrador.getApellido());
        ps.setString(3, administrador.getUsername());
        ps.setString(4, administrador.getPassword());
        ps.setString(5, administrador.getEmail());
        ps.setBoolean(6, administrador.isPuedeEliminar());
        ps.setBoolean(7, administrador.getBaja());
    }
}