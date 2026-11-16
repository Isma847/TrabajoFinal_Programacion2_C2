package DAO;

import Conexion.ConexionDB;
import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;
import Interfaces.BajaLogicaEnDao;
import Modelo.Pelicula;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class PeliculaDao implements BajaLogicaEnDao<Pelicula> {

    @Override
    public int crear(Pelicula obj) throws SQLException {
        String query = "Insert into peliculas (nombre, duracionMinutos, clasificacion, baja, genero, id_director, fechaEstreno) values (?,?,?,?,?,?,?)";
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
            psInsert.setString(7, obj.getFechaEstreno());

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
    public Collection<Pelicula> leerTodos() throws SQLException {
        String query = "Select * from peliculas";
        ArrayList<Pelicula> peliculas = new ArrayList<>();

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement ps = cn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Pelicula peli = new Pelicula(
                        rs.getBoolean("baja"),
                        ClasificacionEdad.valueOf(rs.getString("clasificacion")),
                        rs.getInt("duracionMinutos"),
                        rs.getString("nombre"),
                        GeneroAudiovisual.valueOf(rs.getString("genero")),
                        rs.getInt("id_director"),
                        rs.getString("fechaEstreno"),
                        rs.getInt("id_pelicula")
                );
                peliculas.add(peli);
            }
        }
        return peliculas;
    }

    @Override
    public int actualizar(Pelicula obj) throws SQLException {
        String query = "Update peliculas set nombre = ?, duracionMinutos = ?, clasificacion = ?, genero = ?, id_director = ?, fechaEstreno = ? where id_pelicula = ?";
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
            ps.setString(6, obj.getFechaEstreno());
            ps.setInt(7, obj.getId());

            result = ps.executeUpdate();
        }
        return result;
    }

    @Override
    public int eliminar(int id) throws SQLException {
        String query = "Delete from peliculas where id_pelicula = ?";
        int result = 0;

        try(Connection cn = ConexionDB.conectar();
            PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setInt(1, id);

            result = ps.executeUpdate();
        }
        return result;
    }

    public int bajaLogica(int id, boolean baja) throws SQLException {
        String query = "Update peliculas set baja = ? where id_pelicula = ?";
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