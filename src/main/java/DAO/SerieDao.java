package DAO;

import Conexion.ConexionDB;
import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;
import Interfaces.BajaLogicaEnDao;
import Modelo.Serie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * DAO de series: tabla {@code series}. Funciona igual que PeliculaDao (ver ese DAO para el patrón
 * general y el manejo del director NULL).
 *
 * Lo propio de la serie es que guarda cantidadCapitulos y cantidadTemporadas en lugar de la fecha
 * de estreno.
 */
public class SerieDao implements BajaLogicaEnDao<Serie>{

    /**
     * Igual que PeliculaDao.crear, pero guardando capítulos y temporadas.
     */
    @Override
    public int crear(Serie obj) throws SQLException {
        String query = "Insert into series (nombre, duracionMinutos, clasificacion, baja, genero, id_director, cantidadCapitulos, cantidadTemporadas, descripcion) values (?,?,?,?,?,?,?,?,?)";
        String query_getId = "Select LAST_INSERT_ID()";

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement psInsert = cn.prepareStatement(query);
            PreparedStatement psSelect = cn.prepareStatement(query_getId)) {

            psInsert.setString(1, obj.getNombre());
            psInsert.setInt(2, obj.getDuracionMinutos());
            psInsert.setString(3, obj.getClasificacionEdad().name());
            psInsert.setBoolean(4, obj.getBaja());
            psInsert.setString(5, obj.getGenero().name());
            // Si no tiene director (id 0) se guarda NULL para no violar la FK
            if (obj.getIdDirector() > 0) psInsert.setInt(6, obj.getIdDirector());
            else psInsert.setNull(6, java.sql.Types.INTEGER);
            psInsert.setInt(7, obj.getCantidadCapitulos());
            psInsert.setInt(8, obj.getCantidadTemporadas());
            psInsert.setString(9, obj.getDescripcion());

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
     * Lee todas las series (incluidas las dadas de baja).
     */
    @Override
    public Collection<Serie> leerTodos() throws SQLException {
        String query = "Select * from series";
        ArrayList<Serie> series = new ArrayList<>();

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement ps = cn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Serie serie = new Serie(
                        rs.getBoolean("baja"),
                        ClasificacionEdad.valueOf(rs.getString("clasificacion")),
                        rs.getInt("duracionMinutos"),
                        rs.getString("nombre"),
                        GeneroAudiovisual.valueOf(rs.getString("genero")),
                        rs.getInt("id_director"),
                        rs.getInt("id_serie"),
                        rs.getInt("cantidadCapitulos"),
                        rs.getInt("cantidadTemporadas"),
                        rs.getString("descripcion")
                );
                series.add(serie);
            }
        }
        return series;
    }

    /**
     * Actualiza los datos de la serie por id; no modifica la baja.
     */
    @Override
    public int actualizar(Serie obj) throws SQLException {
        String query = "Update series set nombre = ?, duracionMinutos = ?, clasificacion = ?, genero = ?, id_director = ?, cantidadCapitulos = ?, cantidadTemporadas = ?, descripcion = ? where id_serie = ?";
        int result = 0;

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, obj.getNombre());
            ps.setInt(2, obj.getDuracionMinutos());
            ps.setString(3, obj.getClasificacionEdad().name());
            ps.setString(4, obj.getGenero().name());
            // Si no tiene director (id 0) se guarda NULL para no violar la FK
            if (obj.getIdDirector() > 0) ps.setInt(5, obj.getIdDirector());
            else ps.setNull(5, java.sql.Types.INTEGER);
            ps.setInt(6, obj.getCantidadCapitulos());
            ps.setInt(7, obj.getCantidadTemporadas());
            ps.setString(8, obj.getDescripcion());
            ps.setInt(9, obj.getId());

            result = ps.executeUpdate();
        }
        return result;
    }

    /**
     * Borrado físico; la base borra por cascada su fila del pivote y lo que dependa de ella.
     */
    @Override
    public int eliminar(int id) throws SQLException {
        String query = "Delete from series where id_serie = ?";
        int result = 0;

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setInt(1, id);

            result = ps.executeUpdate();
        }
        return result;
    }

    /**
     * Cambia solo la columna baja de la serie.
     */
    public int bajaLogica(int id, boolean baja) throws SQLException {
        String query = "Update series set baja = ? where id_serie = ?";
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
