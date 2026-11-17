package DAO;

import Conexion.ConexionDB;
import Interfaces.CRUDBase;
import Modelo.Resena;

import java.sql.*;
import java.util.ArrayList;

public class ResenaDao implements CRUDBase<Resena> {

    @Override
    public int crear(Resena resena) throws SQLException {
        String sql = "INSERT INTO resenas (calificacion, comentario, fecha, id_contenido, id_usuario, tipo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cargarParametros(ps, resena);
            ps.setInt(4, resena.getIdContenido());
            ps.setInt(5, resena.getIdUsuario());
            ps.setString(6, resena.getTipo());

            if (ps.executeUpdate() == 0) {
                return -1;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("No se pudo obtener el ID de la reseña");
        }
    }

    @Override
    public ArrayList<Resena> leerTodos() throws SQLException {
        String sql = "SELECT id_resena, id_contenido, id_usuario, tipo, calificacion, comentario, fecha "
                + "FROM resenas";
        ArrayList<Resena> resenas = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Resena resena = new Resena(
                        rs.getInt("id_contenido"),
                        rs.getInt("id_usuario"),
                        rs.getString("tipo"),
                        rs.getInt("calificacion"),
                        rs.getString("comentario"),
                        rs.getString("fecha")
                );
                resena.setId(rs.getInt("id_resena"));
                resenas.add(resena);
            }
        }

        return resenas;
    }

    @Override
    public int actualizar(Resena resena) throws SQLException {
        String sql = "UPDATE resenas SET calificacion = ?, comentario = ?, fecha = ? "
                + "WHERE id_resena = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            cargarParametros(ps, resena);
            ps.setInt(4, resena.getId());

            return ps.executeUpdate();
        }
    }

    @Override
    public int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM resenas WHERE id_resena = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    private void cargarParametros(PreparedStatement ps, Resena resena) throws SQLException {
        ps.setInt(1, resena.getCalificacion());
        ps.setString(2, resena.getComentario());
        ps.setString(3, resena.getFecha());
    }
}
