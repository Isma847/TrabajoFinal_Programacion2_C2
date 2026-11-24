package DAO;

import Conexion.ConexionDB;
import Enumerados.TipoContenido;
import Modelo.RegistroContenido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * DAO de la tabla pivote 'contenidoMultimedia'. Le asigna a cada película, serie o
 * videojuego un "id general" para que reseñas y favoritos puedan apuntar a cualquier tipo de
 * contenido con una sola clave foránea.
 *
 * No implementa CRUDBase, ya que solo tiene crear y leerTodos. No hay actualizar ni eliminar
 * porque la fila del pivote se borra sola en cascada cuando se borra la película, serie
 * o videojuego al que apunta.
 */
public class ContenidoDao {

    public ContenidoDao() {}

    /**
     * Registra un contenido en el pivote y devuelve su id general autogenerado, o -1 si no se
     * insertó. Solo se completa la columna del tipo correspondiente (id_pelicula, id_serie o
     * id_videojuego) y las otras quedan en NULL, como exige el CHECK de la tabla.
     */
    public int crear(TipoContenido tipo, int idPropio) throws SQLException {
        String sql = "INSERT INTO contenidoMultimedia (tipo, " + columnaDe(tipo) + ") VALUES (?, ?)";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tipo.name());
            ps.setInt(2, idPropio);

            if (ps.executeUpdate() == 0) {
                return -1;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("No se pudo obtener el ID general del contenido");
        }
    }

    /**
     * Lee todo el pivote y devuelve una lista de RegistroContenido (id general, tipo e id
     * propio). ContenidoService la usa una sola vez, al iniciar, para armar su registro. Se usa
     * ArrayList porque solo se va a recorrer en orden.
     */
    public ArrayList<RegistroContenido> leerTodos() throws SQLException {
        String sql = "SELECT id_contenidoMultimedia, tipo, id_pelicula, id_serie, id_videojuego "
                + "FROM contenidoMultimedia ORDER BY id_contenidoMultimedia";
        ArrayList<RegistroContenido> registros = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TipoContenido tipo = TipoContenido.valueOf(rs.getString("tipo"));
                registros.add(new RegistroContenido(
                        rs.getInt("id_contenidoMultimedia"),
                        tipo,
                        rs.getInt(columnaDe(tipo))));
            }
        }

        return registros;
    }

    /**
     * Devuelve qué columna del pivote guarda el id propio según el tipo. Se usa al insertar y al
     * leer, así el switch queda en un solo lugar.
     */
    private String columnaDe(TipoContenido tipo) {
        return switch (tipo) {
            case PELICULA -> "id_pelicula";
            case SERIE -> "id_serie";
            case JUEGO -> "id_videojuego";
        };
    }
}
