package Service;

import DAO.VideojuegoDao;
import Enumerados.GeneroVideojuegos;
import Excepciones.DatoInvalidoException;
import Modelo.Videojuego;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Service de videojuegos. Hereda las operaciones comunes de {@code Service<Videojuego>} y agrega
 * su validación y dos filtros propios (género y dificultad). No tiene quitarDirector porque los
 * videojuegos no tienen director.
 */
public class VideojuegoService extends Service<Videojuego> {

    public VideojuegoService() throws SQLException {
        super(new VideojuegoDao());
    }

    /// Solo exige que el videojuego tenga género; lanza DatoInvalidoException si falta.
    @Override
    protected void validar(Videojuego v) {
        if (v.getGenero() == null) {
            throw new DatoInvalidoException("El género es obligatorio");
        }

        if (v.getClasificacionEdad() == null) {
            throw new DatoInvalidoException("La clasificación es obligatoria");
        }

        if (v.getDificultad() <= 0) {
            throw new DatoInvalidoException(
                    "La dificultad debe ser mayor a 0"
            );
        }
    }

    /// Videojuegos de un género según su estado de baja (false = activos).
    public ArrayList<Videojuego> filtrarPorGenero(GeneroVideojuegos genero, boolean baja) {
        ArrayList<Videojuego> resultado = new ArrayList<>();
        for(Videojuego v : getLista(baja)) {
            if(v.getGenero() == genero) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    /// Videojuegos con una dificultad exacta según su estado de baja.
    public ArrayList<Videojuego> filtrarPorDificultad(int dificultad, boolean baja) {
        ArrayList<Videojuego> resultado = new ArrayList<>();
        for(Videojuego v : getLista(baja)) {
            if(v.getDificultad() == dificultad) {
                resultado.add(v);
            }
        }
        return resultado;
    }
}
