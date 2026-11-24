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

/**
 * DAO de películas: acceso a la tabla {@code peliculas} por JDBC. Implementa BajaLogicaEnDao, así
 * que además del CRUD básico ofrece baja lógica.
 *
 * Todos los DAO siguen el mismo patrón: cada método abre su propia conexión con try-with-
 * resources (se cierra sola), usa PreparedStatement con parámetros "?" (evita inyección SQL) y no
 * guarda estado ni colecciones; de eso se ocupa el Service.
 *
 * Particularidad: una película puede no tener director. En el modelo eso se representa con
 * idDirector = 0 y acá se guarda NULL en la base para no violar la clave foránea.
 */
public class PeliculaDao implements BajaLogicaEnDao<Pelicula> {

    /**
     * Inserta la película y devuelve el id que generó la base (también se lo asigna al objeto), o
     * -1 si no se pudo obtener. El id se pide con LAST_INSERT_ID() sobre la misma conexión, que
     * es seguro porque ese valor es propio de cada conexión.
     */
    @Override
    public int crear(Pelicula obj) throws SQLException {
        String query = "Insert into peliculas (nombre, duracionMinutos, clasificacion, baja, genero, id_director, fechaEstreno, descripcion) values (?,?,?,?,?,?,?,?)";
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
            psInsert.setString(8, obj.getDescripcion());

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
     * Lee toda la tabla (incluidas las películas dadas de baja) y arma la lista de objetos. El
     * Service la usa una sola vez, cuando se inicializa.
     */
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
                        rs.getInt("id_pelicula"),
                        rs.getString("descripcion")
                );
                peliculas.add(peli);
            }
        }
        return peliculas;
    }

    /**
     * Actualiza los datos editables de la película por id. No toca la baja (eso es de
     * bajaLogica). Devuelve la cantidad de filas afectadas (0 si el id no existe).
     */
    @Override
    public int actualizar(Pelicula obj) throws SQLException {
        String query = "Update peliculas set nombre = ?, duracionMinutos = ?, clasificacion = ?, genero = ?, id_director = ?, fechaEstreno = ?, descripcion = ? where id_pelicula = ?";
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
            ps.setString(7, obj.getDescripcion());
            ps.setInt(8, obj.getId());

            result = ps.executeUpdate();
        }
        return result;
    }

    /**
     * Borrado físico, definitivo. La base elimina por cascada su fila del pivote y, a través de
     * ella, sus reseñas, favoritos e historial.
     */
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

    /**
     * Baja lógica: solo cambia la columna baja (true = dada de baja, false = reactivada). La fila
     * sigue existiendo.
     */
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