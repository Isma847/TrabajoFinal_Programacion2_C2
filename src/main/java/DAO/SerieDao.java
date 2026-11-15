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

public class SerieDao implements BajaLogicaEnDao<Serie>{

    @Override
    public int crear(Serie obj) throws SQLException {
        String query = "Insert into series (nombre, duracionMinutos, clasificacion, baja, genero, id_director, cantidadCapitulos, cantidadTemporadas) values (?,?,?,?,?,?,?,?)";
        String query_getId = "Select LAST_INSERT_ID()";

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement psInsert = cn.prepareStatement(query);
            PreparedStatement psSelect = cn.prepareStatement(query_getId)) {

            psInsert.setString(1, obj.getNombre());
            psInsert.setInt(2, obj.getDuracionMinutos());
            psInsert.setString(3, obj.getClasificacionEdad().name());
            psInsert.setBoolean(4, obj.getBaja());
            psInsert.setString(5, obj.getGenero().name());
            psInsert.setInt(6, obj.getIdDirector());
            psInsert.setInt(7, obj.getCantidadCapitulos());
            psInsert.setInt(8, obj.getCantidadTemporadas());

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
                        rs.getInt("cantidadTemporadas")
                );
                series.add(serie);
            }
        }
        return series;
    }

    @Override
    public int actualizar(Serie obj) throws SQLException {
        String query = "Update series set nombre = ?, duracionMinutos = ?, clasificacion = ?, genero = ?, id_director = ?, cantidadCapitulos = ?, cantidadTemporadas = ? where id_serie = ?";
        int result = 0;

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, obj.getNombre());
            ps.setInt(2, obj.getDuracionMinutos());
            ps.setString(3, obj.getClasificacionEdad().name());
            ps.setString(4, obj.getGenero().name());
            ps.setInt(5, obj.getIdDirector());
            ps.setInt(6, obj.getCantidadCapitulos());
            ps.setInt(7, obj.getCantidadTemporadas());
            ps.setInt(8, obj.getId());

            result = ps.executeUpdate();
        }
        return result;
    }

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
