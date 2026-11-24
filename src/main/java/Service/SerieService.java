package Service;

import DAO.SerieDao;
import Enumerados.GeneroAudiovisual;
import Excepciones.DatoInvalidoException;
import Modelo.Serie;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Service de series. Igual que PeliculaService, hereda las operaciones comunes de {@code
 * Service<Serie>} y solo agrega su validación, un filtro por género y quitarDirector.
 */
public class SerieService extends Service<Serie> {

    public SerieService() throws SQLException {
        super(new SerieDao());
    }

    /// Solo exige que la serie tenga género; lanza DatoInvalidoException si falta.
    @Override
    protected void validar(Serie s) {
        if(s.getGenero() == null) {
            throw new DatoInvalidoException("El género es obligatorio");
        }
    }

    /// Series de un género según su estado de baja (false = activas).
    public ArrayList<Serie> filtrarPorGenero(GeneroAudiovisual genero, boolean baja) {
        ArrayList<Serie> resultado = new ArrayList<>();
        for(Serie s : getLista(baja)) {
            if(s.getGenero() == genero) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    /**
     * Igual que PeliculaService.quitarDirector: deja sin director (id 0) a las series del
     * director eliminado, para que la memoria coincida con la base.
     */
    public void quitarDirector(int idDirector) {
        for(Serie s : coleccion.values()) {
            if(s.getIdDirector() == idDirector) {
                s.setIdDirector(0);
            }
        }
    }
}
