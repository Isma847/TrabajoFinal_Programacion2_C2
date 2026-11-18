package DAO;

import Conexion.ConexionDB;
import Modelo.Favorito;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class FavoritoDao {

    public int crear(Favorito favorito) throws SQLException {
        String sql = "INSERT INTO favoritos (id_usuario, tipo, id_contenido) VALUES (?, ?, ?)";

        try (Connection cn = ConexionDB.conectar()) {
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, favorito.getId_usaurio());
                ps.setString(2, favorito.getTipo());
                ps.setInt(3, favorito.getId_contenido());
                if (ps.executeUpdate() == 0) {
                    cn.rollback();
                    return -1;
                }

                int id;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("No se pudo obtener el ID del favorito");
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


    public Collection<Favorito> leerTodos() throws SQLException {
        String sql = "SELECT * FROM favoritos";
        ArrayList<Favorito> favoritos = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Favorito favorito = new Favorito(
                        rs.getInt("id_favorito"),
                        rs.getInt("id_usuario"),
                        rs.getInt("id_contenido"),
                        rs.getString("tipo")
                );
                favoritos.add(favorito);
            }
        }

        return favoritos;
    }

    public int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM favoritos WHERE id_favorito = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public int eliminarPorUsuario(int idUsuario) throws SQLException {
        String sql = "DELETE FROM favoritos WHERE id_usuario = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate();
        }
    }

    public int quitarDeUsuario(int idUsuario, int idContenido, String tipo) throws SQLException {
        String sql = "DELETE FROM favoritos WHERE id_usuario = ? AND id_contenido = ? AND tipo = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idContenido);
            ps.setString(3, tipo);
            return ps.executeUpdate();
        }
    }


}
