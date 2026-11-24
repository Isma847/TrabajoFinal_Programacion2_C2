package DAO;

import Conexion.ConexionDB;
import Enumerados.ClasificacionEdad;
import Enumerados.GeneroVideojuegos;
import Interfaces.BajaLogicaEnDao;
import Modelo.Videojuego;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * DAO de videojuegos: tabla {@code videojuegos}. Funciona igual que PeliculaDao (ver ese DAO para
 * el patrón general).
 *
 * Diferencias: el videojuego no tiene director ni fecha de estreno; en cambio guarda la
 * dificultad y su género es del enum GeneroVideojuegos.
 */
public class VideojuegoDao implements BajaLogicaEnDao<Videojuego> {
    /**
     * Igual que PeliculaDao.crear, pero guardando la dificultad.
     */
    @Override
    public int crear(Videojuego obj) throws SQLException {
        String query = "Insert into videojuegos (nombre, duracionMinutos, clasificacion, baja, genero, dificultad, descripcion) values (?,?,?,?,?,?,?)";
        String query_getId = "Select LAST_INSERT_ID()";

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement psInsert = cn.prepareStatement(query);
            PreparedStatement psSelect = cn.prepareStatement(query_getId)) {

            psInsert.setString(1, obj.getNombre());
            psInsert.setInt(2, obj.getDuracionMinutos());
            psInsert.setString(3, obj.getClasificacionEdad().name());
            psInsert.setBoolean(4, obj.getBaja());
            psInsert.setString(5, obj.getGenero().name());
            psInsert.setInt(6, obj.getDificultad());
            psInsert.setString(7, obj.getDescripcion());

            psInsert.executeUpdate();

            try (ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) {
                    int ultimoId = rs.getInt(1);
                    obj.setId(ultimoId);
                    return ultimoId;
                }
            }
        }
        return -1;
    }

    /**
     * Lee todos los videojuegos (incluidos los dados de baja).
     */
    @Override
    public Collection<Videojuego> leerTodos() throws SQLException {
        String query = "Select * from videojuegos";
        ArrayList<Videojuego> videojuegos = new ArrayList<>();

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement ps = cn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Videojuego juego = new Videojuego(
                        rs.getBoolean("baja"),
                        ClasificacionEdad.valueOf(rs.getString("clasificacion")),
                        rs.getInt("duracionMinutos"),
                        rs.getString("nombre"),
                        rs.getInt("id_videojuego"),
                        rs.getInt("dificultad"),
                        GeneroVideojuegos.valueOf(rs.getString("genero")),
                        rs.getString("descripcion")
                );
                videojuegos.add(juego);
            }
        }
        return videojuegos;
    }

    /**
     * Actualiza los datos del videojuego por id; no modifica la baja.
     */
    @Override
    public int actualizar(Videojuego obj) throws SQLException {
        String query = "Update videojuegos set nombre = ?, duracionMinutos = ?, clasificacion = ?, genero = ?, dificultad = ?, descripcion = ? where id_videojuego = ?";
        int result = 0;

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, obj.getNombre());
            ps.setInt(2, obj.getDuracionMinutos());
            ps.setString(3, obj.getClasificacionEdad().name());
            ps.setString(4, obj.getGenero().name());
            ps.setInt(5, obj.getDificultad());
            ps.setString(6, obj.getDescripcion());
            ps.setInt(7, obj.getId());

            result = ps.executeUpdate();
        }
        return result;
    }

    /**
     * Borrado físico; la base borra por cascada su fila del pivote, lo que dependa de ella y sus
     * progresos.
     */
    @Override
    public int eliminar(int id) throws SQLException {
        String query = "Delete from videojuegos where id_videojuego = ?";
        int result = 0;

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setInt(1, id);

            result = ps.executeUpdate();
        }
        return result;
    }

    /**
     * Cambia solo la columna baja del videojuego.
     */
    public int bajaLogica(int id, boolean baja) throws SQLException {
        String query = "Update videojuegos set baja = ? where id_videojuego = ?";
        int result = 0;

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setBoolean(1, baja);
            ps.setInt(2, id);

            result = ps.executeUpdate();
        }
        return result;
    }
}
