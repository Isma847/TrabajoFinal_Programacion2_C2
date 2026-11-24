package DAO;

import Conexion.ConexionDB;
import Interfaces.CRUDBase;
import Modelo.Resena;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO de reseñas: tabla {@code resenas}. Implementa solo CRUDBase porque las reseñas no tienen
 * baja lógica: se borran de verdad.
 *
 * La reseña apunta al contenido por el id general (pivote) y la base garantiza una sola reseña
 * por usuario y contenido con un UNIQUE.
 */
public class ResenaDao implements CRUDBase<Resena> {

    /**
     * Inserta la reseña y devuelve su id generado, o -1 si no se insertó.
     */
    @Override
    public int crear(Resena resena) throws SQLException {
        String sql = "INSERT INTO resenas (calificacion, comentario, fecha, id_contenido, id_usuario) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cargarParametros(ps, resena);
            ps.setInt(4, resena.getIdContenido());
            ps.setInt(5, resena.getIdUsuario());

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

    /**
     * Lee todas las reseñas; el Service la usa al iniciarse.
     */
    @Override
    public ArrayList<Resena> leerTodos() throws SQLException {
        String sql = "SELECT id_resena, id_contenido, id_usuario, calificacion, comentario, fecha "
                + "FROM resenas";
        ArrayList<Resena> resenas = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Resena resena = new Resena(
                        rs.getInt("id_contenido"),
                        rs.getInt("id_usuario"),
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

    /**
     * Solo se pueden cambiar calificación, comentario y fecha; el usuario y el contenido de la
     * reseña no se modifican.
     */
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

    /**
     * Elimina la reseña por su id.
     */
    @Override
    public int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM resenas WHERE id_resena = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    /**
     * Carga los tres datos que comparten el INSERT y el UPDATE (calificación, comentario y fecha,
     * posiciones 1 a 3). Después cada método agrega los que le faltan.
     */
    private void cargarParametros(PreparedStatement ps, Resena resena) throws SQLException {
        ps.setInt(1, resena.getCalificacion());
        ps.setString(2, resena.getComentario());
        ps.setString(3, resena.getFecha());
    }
}
