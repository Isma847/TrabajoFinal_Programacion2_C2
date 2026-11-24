package DAO;

import Conexion.ConexionDB;
import Interfaces.CRUDBase;
import Modelo.Director;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

/**
 * DAO de directores: tabla {@code directores}. Implementa solo CRUDBase porque los directores no
 * tienen baja lógica: solo se pueden eliminar de verdad.
 *
 * Al borrar un director no se borra su contenido: la clave foránea tiene ON DELETE SET NULL y
 * deja id_director en NULL en sus películas y series.
 */
public class DirectorDao implements CRUDBase<Director> {


    /**
     * Inserta el director y devuelve el id generado (se pide con RETURN_GENERATED_KEYS), o -1 si
     * no se insertó.
     */
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

    /**
     * Lee todos los directores. El Service la usa una sola vez, al iniciarse.
     */
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

    /**
     * Actualiza nombre, apellido y descripción del director por id.
     */
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

    /**
     * Borrado físico del director. Ojo: la base pone en NULL el director de sus películas y
     * series, pero las colecciones en memoria no se enteran; para eso existe quitarDirector en
     * PeliculaService y SerieService.
     */
    @Override
    public int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM directores WHERE id_director = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

}