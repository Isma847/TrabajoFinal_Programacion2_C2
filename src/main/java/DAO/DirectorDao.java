package DAO;

import Conexion.ConexionDB;
import Modelo.Director;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class DirectorDao {

    public int crear(Director director) throws SQLException {
        String sql = "INSERT INTO directores (nombre, apellido, descripcion) VALUES (?, ?, ?)";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, director.getNombre());
            ps.setString(2, director.getApellido());
            ps.setString(3, director.getDescripcion());

            if (ps.executeUpdate() == 0) {
                return -1;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("No se pudo obtener el ID del director");
        }
    }

    public Collection<Director> leerTodos() throws SQLException {
        String sql = "SELECT id_director, nombre, apellido, descripcion FROM directores";
        ArrayList<Director> directores = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Director director = new Director(
                        rs.getString("apellido"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
                director.setId(rs.getInt("id_director"));
                directores.add(director);
            }
        }

        return directores;
    }

    public int actualizar(Director director) throws SQLException {
        String sql = "UPDATE directores SET nombre = ?, apellido = ?, descripcion = ? "
                + "WHERE id_director = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, director.getNombre());
            ps.setString(2, director.getApellido());
            ps.setString(3, director.getDescripcion());
            ps.setInt(4, director.getId());

            return ps.executeUpdate();
        }
    }
}