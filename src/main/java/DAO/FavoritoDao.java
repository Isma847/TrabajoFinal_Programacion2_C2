package DAO;

import Conexion.ConexionDB;
import Modelo.Favorito;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * DAO de favoritos: tabla {@code favoritos}, que relaciona un usuario con un contenido (por el id
 * general del pivote). No implementa CRUDBase porque un favorito nunca se modifica: se agrega o
 * se quita, por eso no tiene actualizar.
 *
 * La base impide repetir el par (usuario, contenido) con un UNIQUE; el Service lo controla antes
 * para no llegar a ese error.
 */
public class FavoritoDao {

    /**
     * Inserta el favorito y devuelve su id generado, o -1 si no se insertó. Usa una transacción
     * manual (setAutoCommit(false)): si algo falla se hace rollback, si sale bien commit.
     */
    public int crear(Favorito favorito) throws SQLException {
        String sql = "INSERT INTO favoritos (id_usuario, id_contenido) VALUES (?, ?)";

        try (Connection cn = ConexionDB.conectar()) {
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, favorito.getId_usaurio());
                ps.setInt(2, favorito.getId_contenido());
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


    /**
     * Lee todos los favoritos de todos los usuarios; el Service la usa al iniciarse.
     */
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
                        rs.getInt("id_contenido")
                );
                favoritos.add(favorito);
            }
        }

        return favoritos;
    }

    /**
     * Elimina un favorito por su propio id.
     */
    public int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM favoritos WHERE id_favorito = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    /**
     * Elimina todos los favoritos de un usuario. Devuelve cuántos borró.
     */
    public int eliminarPorUsuario(int idUsuario) throws SQLException {
        String sql = "DELETE FROM favoritos WHERE id_usuario = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate();
        }
    }

    /**
     * Elimina el favorito de un usuario para un contenido puntual (es lo que hace el usuario al
     * quitar un contenido de su lista).
     */
    public int quitarDeUsuario(int idUsuario, int idContenido) throws SQLException {
        String sql = "DELETE FROM favoritos WHERE id_usuario = ? AND id_contenido = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idContenido);
            return ps.executeUpdate();
        }
    }
}