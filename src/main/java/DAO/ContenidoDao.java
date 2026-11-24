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

// Acceso a la tabla pivote contenidoMultimedia.
// No hay actualizar ni eliminar: la fila del pivote se borra sola (ON DELETE CASCADE)
// cuando se borra la pelicula, serie o videojuego al que apunta.
public class ContenidoDao {

    public ContenidoDao() {}

    // Registra un contenido y devuelve su id general
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

    // Columna del pivote que guarda el id propio de cada tipo
    private String columnaDe(TipoContenido tipo) {
        return switch (tipo) {
            case PELICULA -> "id_pelicula";
            case SERIE -> "id_serie";
            case JUEGO -> "id_videojuego";
        };
    }
}
